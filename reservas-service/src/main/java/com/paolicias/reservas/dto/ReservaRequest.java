package com.paolicias.reservas.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Lo que envía la app al crear una reserva. El estado y el total los pone el servidor.
// Mismas reglas que Validaciones.kt.
public record ReservaRequest(
        @NotBlank(message = "Ingresa el nombre del evento")
        @Size(min = 3, max = 100, message = "El nombre del evento debe tener al menos 3 letras")
        String nombreEvento,

        @NotNull(message = "Elige la fecha del evento")
        @FutureOrPresent(message = "La fecha debe ser de hoy en adelante")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate fecha,

        @NotNull(message = "Elige el horario del evento")
        @JsonFormat(pattern = "HH:mm")
        LocalTime horario,

        @NotNull(message = "Ingresa la cantidad de invitados")
        @Min(value = 1, message = "Los invitados deben ser entre 1 y 100")
        @Max(value = 100, message = "Los invitados deben ser entre 1 y 100")
        Integer invitados,

        @Size(max = 500, message = "Las observaciones son muy largas")
        String observaciones,

        // Correo del cliente con sesión iniciada (el login vive en SQLite, en la app)
        @NotBlank(message = "Falta el correo del cliente")
        @Email(message = "Correo no válido")
        String correoCliente,

        List<Long> productoIds
) {
}
