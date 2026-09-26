package com.eventify.semana_4.dto;

import java.time.LocalDateTime;

public record EventSummaryDTO(
        String nombre,
        LocalDateTime fecha,
        String venueNombre,
        String ciudad
) {}
