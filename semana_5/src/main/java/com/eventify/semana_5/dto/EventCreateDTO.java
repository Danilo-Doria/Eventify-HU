package com.eventify.semana_5.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record EventCreateDTO (
        String nombre,
        LocalDateTime fecha,
        String descripcion,
        Long venueId,
        Set<Long> categoryIds
){}
