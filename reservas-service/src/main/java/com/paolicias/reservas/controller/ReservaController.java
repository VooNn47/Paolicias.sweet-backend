package com.paolicias.reservas.controller;

import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.paolicias.reservas.dto.CalculoInsumos;
import com.paolicias.reservas.dto.ReservaRequest;
import com.paolicias.reservas.entities.Reserva;
import com.paolicias.reservas.services.ReservaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // GET /api/reservas?correo=...&estado=...  ->  ordenadas por fecha y horario
    //   Mi Agenda:   ?correo=cliente@correo.cl
    //   Panel admin: ?estado=En revisión   (sin filtros o "Todas" = todas)
    @GetMapping
    public List<Reserva> listarReservas(@RequestParam(required = false) String correo,
                                        @RequestParam(required = false) String estado) {
        return reservaService.listar(correo, estado);
    }

    // GET /api/reservas/{id}  ->  una reserva, o 404 si no existe
    @GetMapping("/{id}")
    public Reserva obtenerPorId(@PathVariable Long id) {
        return reservaService.obtener(id);
    }

    // POST /api/reservas  ->  crea una reserva (201). El total lo calcula el servidor. 409 si es duplicada.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva guardarReserva(@Valid @RequestBody ReservaRequest request) {
        return reservaService.crear(request);
    }

    // POST /api/reservas/{id}/comprobante?correo=...  (multipart, campo "archivo")  ->  "En revisión"
    @PostMapping(value = "/{id}/comprobante", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Reserva adjuntarComprobante(@PathVariable Long id,
                                       @RequestParam String correo,
                                       @RequestPart("archivo") MultipartFile archivo) {
        return reservaService.adjuntarComprobante(id, correo, archivo);
    }

    // GET /api/reservas/{id}/comprobante  ->  la imagen del comprobante
    @GetMapping("/{id}/comprobante")
    public ResponseEntity<Resource> verComprobante(@PathVariable Long id) {
        ReservaService.ArchivoComprobante archivo = reservaService.leerComprobante(id);
        MediaType tipo = archivo.tipo() != null ? MediaType.parseMediaType(archivo.tipo()) : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok().contentType(tipo).body(new FileSystemResource(archivo.ruta()));
    }

    // GET /api/reservas/{id}/insumos  ->  insumos necesarios, stock y faltantes (botón "Calcular insumos")
    @GetMapping("/{id}/insumos")
    public CalculoInsumos calcularInsumos(@PathVariable Long id) {
        return reservaService.calcularInsumos(id);
    }

    // PATCH /api/reservas/{id}/validar  ->  descuenta los insumos (409 si faltan) y pasa a "Pagado 50%"
    @PatchMapping("/{id}/validar")
    public Reserva validar(@PathVariable Long id) {
        return reservaService.validarAbono(id);
    }

    // PATCH /api/reservas/{id}/rechazar  ->  vuelve a "Pendiente" y se borra el comprobante
    @PatchMapping("/{id}/rechazar")
    public Reserva rechazar(@PathVariable Long id) {
        return reservaService.rechazarAbono(id);
    }

    // PATCH /api/reservas/{id}/completar  ->  "Completado" (después del evento)
    @PatchMapping("/{id}/completar")
    public Reserva completar(@PathVariable Long id) {
        return reservaService.marcarCompletada(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarReserva(@PathVariable Long id) {
    reservaService.eliminar(id);}
}
