package com.paolicias.reservas.services;

import java.nio.file.Path;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.paolicias.reservas.dto.CalculoInsumos;
import com.paolicias.reservas.dto.ReservaRequest;
import com.paolicias.reservas.entities.Reserva;

public interface ReservaService {

    Reserva crear(ReservaRequest request);

    // correo y estado son filtros opcionales (Mi Agenda filtra por correo, el panel admin por estado)
    List<Reserva> listar(String correo, String estado);

    Reserva obtener(Long id);
    
    void eliminar(Long id);

    Reserva adjuntarComprobante(Long id, String correo, MultipartFile archivo);

    ArchivoComprobante leerComprobante(Long id);

    Reserva validarAbono(Long id);

    Reserva rechazarAbono(Long id);

    Reserva marcarCompletada(Long id);

    // Cuánto se necesita de cada insumo para la reserva y si alcanza el inventario
    CalculoInsumos calcularInsumos(Long id);

    record ArchivoComprobante(Path ruta, String tipo) {
    }
}
