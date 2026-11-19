package com.paolicias.reservas.services;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.reservas.client.CotizacionClient;
import com.paolicias.reservas.client.InsumosClient;
import com.paolicias.reservas.dto.CalculoInsumos;
import com.paolicias.reservas.dto.Cotizacion;
import com.paolicias.reservas.dto.LineaProducto;
import com.paolicias.reservas.dto.ReservaRequest;
import com.paolicias.reservas.entities.EstadoAbono;
import com.paolicias.reservas.entities.Reserva;
import com.paolicias.reservas.repository.ReservaRepository;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepo;
    private final CotizacionClient cotizacionClient;
    private final InsumosClient insumosClient;
    private final AlmacenComprobantes almacen;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              CotizacionClient cotizacionClient,
                              InsumosClient insumosClient,
                              AlmacenComprobantes almacenComprobantes) {
        this.reservaRepo = reservaRepository;
        this.cotizacionClient = cotizacionClient;
        this.insumosClient = insumosClient;
        this.almacen = almacenComprobantes;
    }

    // Igual que en la app: trim() + lowercase()
    static String normalizar(String correo) {
        return correo == null ? null : correo.trim().toLowerCase();
    }

    @Override
    public Reserva crear(ReservaRequest req) {
        String correo = normalizar(req.correoCliente());
        String nombreEvento = req.nombreEvento().trim();

        // Evita el doble envío (dos toques seguidos en "Enviar reserva")
        if (reservaRepo.existsByCorreoClienteAndNombreEventoAndFechaAndHorario(
                correo, nombreEvento, req.fecha(), req.horario())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya tienes esta reserva registrada");
        }

        Cotizacion cotizacion = cotizacionClient.cotizar(req.invitados(), req.productoIds());

        Reserva r = new Reserva();
        r.setNombreEvento(nombreEvento);
        r.setFecha(req.fecha());
        r.setHorario(req.horario());
        r.setInvitados(req.invitados());
        r.setObservaciones(req.observaciones() == null ? "" : req.observaciones().trim());
        r.setCorreoCliente(correo);
        r.setEstadoAbono(EstadoAbono.PENDIENTE);
        r.setProductos(cotizacion.lineas().stream()
                .map(l -> String.valueOf(l.productoId()))
                .collect(Collectors.joining(",")));
        r.setDetalleProductos(cotizacion.lineas().stream()
                .map(l -> l.productoId() + ":" + l.unidades())
                .collect(Collectors.joining(",")));
        r.setTarifaServicio(cotizacion.servicio());
        r.setTotal(cotizacion.total());
        return reservaRepo.save(r);
    }

    // "1:3,7:25" -> [(1, 3), (7, 25)]
    static List<LineaProducto> lineasDe(Reserva r) {
        String detalle = r.getDetalleProductos();
        if (detalle == null || detalle.isBlank()) {
            return List.of();
        }
        return Arrays.stream(detalle.split(","))
                .map(par -> par.split(":"))
                .map(p -> new LineaProducto(Long.parseLong(p[0].trim()), Integer.parseInt(p[1].trim())))
                .toList();
    }

    @Override
    public CalculoInsumos calcularInsumos(Long id) {
        return insumosClient.calcular(lineasDe(obtener(id)));
    }

    @Override
    public List<Reserva> listar(String correo, String estado) {
        boolean porCorreo = correo != null && !correo.isBlank();
        boolean porEstado = estado != null && !estado.isBlank() && !"Todas".equalsIgnoreCase(estado);

        List<Reserva> lista = porCorreo
                ? reservaRepo.findByCorreoClienteOrderByFechaAscHorarioAsc(normalizar(correo))
                : porEstado
                        ? reservaRepo.findByEstadoAbonoOrderByFechaAscHorarioAsc(estado)
                        : reservaRepo.findAllByOrderByFechaAscHorarioAsc();

        if (porCorreo && porEstado) {
            lista = lista.stream().filter(r -> estado.equals(r.getEstadoAbono())).toList();
        }
        return lista;
    }

    @Override
    public Reserva obtener(Long id) {
        return reservaRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La reserva no existe"));
    }


    @Override
    public void eliminar(Long id) {
        Reserva r = obtener(id);

     
        String comprobante = r.getComprobante();

        
        reservaRepo.delete(r);

        almacen.borrar(comprobante);
    }

    @Override
    public Reserva adjuntarComprobante(Long id, String correo, MultipartFile archivo) {
        Reserva r = obtener(id);
        // Solo el cliente dueño de la reserva puede subir su comprobante
        if (!r.getCorreoCliente().equals(normalizar(correo))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta reserva no es tuya");
        }
        // Igual que en la app: con el abono ya validado no se puede cambiar el comprobante
        if (!EstadoAbono.PENDIENTE.equals(r.getEstadoAbono())
                && !EstadoAbono.EN_REVISION.equals(r.getEstadoAbono())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El abono de esta reserva ya fue validado");
        }

        String anterior = r.getComprobante();
        r.setComprobante(almacen.guardar(id, archivo));
        r.setComprobanteTipo(archivo.getContentType());
        r.setEstadoAbono(EstadoAbono.EN_REVISION);
        Reserva guardada = reservaRepo.save(r);
        almacen.borrar(anterior); // no deja imágenes huérfanas
        return guardada;
    }

    @Override
    public ArchivoComprobante leerComprobante(Long id) {
        Reserva r = obtener(id);
        if (!r.isTieneComprobante()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Esta reserva no tiene comprobante");
        }
        return new ArchivoComprobante(almacen.ruta(r.getComprobante()), r.getComprobanteTipo());
    }

    @Override
    public Reserva validarAbono(Long id) {
        Reserva r = obtener(id);
        exigirEstado(r, EstadoAbono.EN_REVISION, "Solo se puede validar un abono que está En revisión");
        // Antes de aceptar, se descuentan los insumos del inventario.
        // Si falta algo, insumos-service responde 409 y la reserva sigue En revisión.
        insumosClient.consumir(r.getId(), lineasDe(r));
        r.setEstadoAbono(EstadoAbono.PAGADO);
        r.setFechaPago(LocalDate.now());
        return reservaRepo.save(r);
    }

    @Override
    public Reserva rechazarAbono(Long id) {
        Reserva r = obtener(id);
        exigirEstado(r, EstadoAbono.EN_REVISION, "Solo se puede rechazar un abono que está En revisión");
        // Se borra el comprobante para que el cliente adjunte uno nuevo
        String anterior = r.getComprobante();
        r.setEstadoAbono(EstadoAbono.PENDIENTE);
        r.setComprobante(null);
        r.setComprobanteTipo(null);
        r.setFechaPago(null);
        Reserva guardada = reservaRepo.save(r);
        almacen.borrar(anterior);
        return guardada;
    }

    @Override
    public Reserva marcarCompletada(Long id) {
        Reserva r = obtener(id);
        exigirEstado(r, EstadoAbono.PAGADO, "Solo se puede completar una reserva con el abono pagado");
        r.setEstadoAbono(EstadoAbono.COMPLETADO);
        return reservaRepo.save(r);
    }

    private void exigirEstado(Reserva r, String estado, String mensaje) {
        if (!estado.equals(r.getEstadoAbono())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
        }
    }
}
