package com.eventify.semana_5.dto;

public record VenueResponseDTO(
        Long id,
        String nombre,
        String direccion,
        Integer capacidad,
        String ciudad
) {}