package com.eventify.semana_5.dto;

import java.time.LocalDateTime;

// DTO utilizado para mostrar únicamente la información necesaria de un evento en listados.
public record EventSummaryDTO(
        String nombre,
        LocalDateTime fecha,
        String venueNombre,
        String ciudad
) {}
