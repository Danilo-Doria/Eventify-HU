package com.eventify.semana_5.dto;

import com.eventify.semana_5.validation.NoPastEvents;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Set;

public record EventCreateDTO (
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
        String nombre,

        @NotNull(message = "La fecha es obligatoria")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @NoPastEvents(message = "La fecha del evento debe ser posterior o igual al momento actual")
        LocalDateTime fecha,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String descripcion,

        @NotNull(message = "El id de la sede (venueId) es obligatorio")
        Long venueId,

        @NotEmpty(message = "Debe asignar al menos una categoría")
        Set<Long> categoryIds
){}
