package com.eventify.semana_5.dto;

public record VenueCreateDTO(
        String nombre,
        String direccion,
        Integer capacidad,
        String ciudad
) {}