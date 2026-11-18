package com.paolicias.reservas.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.paolicias.reservas.dto.Cotizacion;
import com.paolicias.reservas.dto.ReservaRequest;
import com.paolicias.reservas.entities.EstadoAbono;
import com.paolicias.reservas.entities.Reserva;
import com.paolicias.reservas.repository.ReservaRepository;

// Pruebas unitarias del servicio: repositorio, cotización y almacén se simulan con Mockito.
class ReservaServiceImplTest {

    private static final String CLIENTE = "ana@correo.cl";

    private ReservaRepository repo;
    private CotizacionService cotizacion;
    private AlmacenComprobantes almacen;
    private ReservaServiceImpl service;

    @BeforeEach
    void setUp() {
        repo = mock(ReservaRepository.class);
        cotizacion = mock(CotizacionService.class);
        almacen = mock(AlmacenComprobantes.class);
        service = new ReservaServiceImpl(repo, cotizacion, almacen);
        when(repo.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Reserva reserva(String estado) {
        Reserva r = new Reserva();
        r.setId(1L);
        r.setNombreEvento("Cumpleaños");
        r.setFecha(LocalDate.now().plusDays(10));
        r.setHorario(LocalTime.of(20, 0));
        r.setInvitados(25);
        r.setCorreoCliente(CLIENTE);
        r.setEstadoAbono(estado);
        r.setTotal(215000);
        return r;
    }

    private ReservaRequest request() {
        return new ReservaRequest(" Cumpleaños ", LocalDate.now().plusDays(10), LocalTime.of(20, 0),
                25, "Sin nueces", " Ana@Correo.CL ", List.of(1L));
    }

    @Test
    void crear_normalizaElCorreoYUsaElTotalDelServidor() {
        when(cotizacion.calcular(25, List.of(1L))).thenReturn(new Cotizacion(25, 80000,
                List.of(new Cotizacion.Linea(1L, "Torta", 3, 45000, 135000)), 215000, 107500));

        Reserva r = service.crear(request());

        assertEquals(CLIENTE, r.getCorreoCliente());
        assertEquals("Cumpleaños", r.getNombreEvento());
        assertEquals(EstadoAbono.PENDIENTE, r.getEstadoAbono());
        assertEquals(215000, r.getTotal());
        assertEquals(107500, r.getAbono());
        assertEquals("1", r.getProductos());
    }

    @Test
    void crear_reservaDuplicada_lanza409() {
        when(repo.existsByCorreoClienteAndNombreEventoAndFechaAndHorario(any(), any(), any(), any()))
                .thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.crear(request()));

        assertEquals(409, ex.getStatusCode().value());
        verify(repo, never()).save(any());
    }

    @Test
    void listar_porCorreo_esMiAgenda() {
        when(repo.findByCorreoClienteOrderByFechaAscHorarioAsc(CLIENTE))
                .thenReturn(List.of(reserva(EstadoAbono.PENDIENTE)));

        assertEquals(1, service.listar("ANA@correo.cl", null).size());
    }

    @Test
    void listar_porEstado_esElPanelAdmin() {
        when(repo.findByEstadoAbonoOrderByFechaAscHorarioAsc(EstadoAbono.EN_REVISION))
                .thenReturn(List.of(reserva(EstadoAbono.EN_REVISION)));

        assertEquals(1, service.listar(null, EstadoAbono.EN_REVISION).size());
    }

    @Test
    void listar_todas_usaLaConsultaGeneral() {
        when(repo.findAllByOrderByFechaAscHorarioAsc()).thenReturn(List.of(reserva(EstadoAbono.PENDIENTE)));

        assertEquals(1, service.listar(null, "Todas").size());
    }

    @Test
    void obtener_inexistente_lanza404() {
        when(repo.findById(9L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.obtener(9L));

        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void adjuntarComprobante_pasaAEnRevisionYBorraElAnterior() {
        Reserva r = reserva(EstadoAbono.EN_REVISION);
        r.setComprobante("viejo.jpg");
        when(repo.findById(1L)).thenReturn(Optional.of(r));
        MockMultipartFile img = new MockMultipartFile("archivo", "c.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(almacen.guardar(1L, img)).thenReturn("nuevo.jpg");

        Reserva res = service.adjuntarComprobante(1L, CLIENTE, img);

        assertEquals(EstadoAbono.EN_REVISION, res.getEstadoAbono());
        assertEquals("nuevo.jpg", res.getComprobante());
        verify(almacen).borrar("viejo.jpg");
    }

    @Test
    void adjuntarComprobante_deOtroCliente_lanza403() {
        when(repo.findById(1L)).thenReturn(Optional.of(reserva(EstadoAbono.PENDIENTE)));
        MockMultipartFile img = new MockMultipartFile("archivo", "c.jpg", "image/jpeg", new byte[]{1});

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.adjuntarComprobante(1L, "otro@correo.cl", img));

        assertEquals(403, ex.getStatusCode().value());
    }

    @Test
    void adjuntarComprobante_conAbonoPagado_lanza409() {
        when(repo.findById(1L)).thenReturn(Optional.of(reserva(EstadoAbono.PAGADO)));
        MockMultipartFile img = new MockMultipartFile("archivo", "c.jpg", "image/jpeg", new byte[]{1});

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.adjuntarComprobante(1L, CLIENTE, img));

        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void validarAbono_enRevision_quedaPagadoConFecha() {
        when(repo.findById(1L)).thenReturn(Optional.of(reserva(EstadoAbono.EN_REVISION)));

        Reserva r = service.validarAbono(1L);

        assertEquals(EstadoAbono.PAGADO, r.getEstadoAbono());
        assertEquals(LocalDate.now(), r.getFechaPago());
    }

    @Test
    void validarAbono_sinComprobante_lanza409() {
        when(repo.findById(1L)).thenReturn(Optional.of(reserva(EstadoAbono.PENDIENTE)));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.validarAbono(1L));

        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void rechazarAbono_vuelveAPendienteYBorraElComprobante() {
        Reserva r = reserva(EstadoAbono.EN_REVISION);
        r.setComprobante("c.jpg");
        when(repo.findById(1L)).thenReturn(Optional.of(r));

        Reserva res = service.rechazarAbono(1L);

        assertEquals(EstadoAbono.PENDIENTE, res.getEstadoAbono());
        assertNull(res.getComprobante());
        assertNull(res.getFechaPago());
        verify(almacen).borrar("c.jpg");
    }

    @Test
    void marcarCompletada_soloDesdePagado() {
        when(repo.findById(1L)).thenReturn(Optional.of(reserva(EstadoAbono.PAGADO)));

        assertEquals(EstadoAbono.COMPLETADO, service.marcarCompletada(1L).getEstadoAbono());
    }

    @Test
    void marcarCompletada_sinPagar_lanza409() {
        when(repo.findById(1L)).thenReturn(Optional.of(reserva(EstadoAbono.PENDIENTE)));

        assertThrows(ResponseStatusException.class, () -> service.marcarCompletada(1L));
    }
}
