package com.eventify.semana_5.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record EventResponseDTO(
        Long id,
        String nombre,
        LocalDateTime fecha,
        String descripcion,
        String venueName,
        Set<String> categoryNames
) {}
