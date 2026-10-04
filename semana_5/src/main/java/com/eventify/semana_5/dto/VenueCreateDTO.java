package com.eventify.semana_5.dto;

import jakarta.validation.constraints.*;

public record VenueCreateDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @NotBlank(message = "La dirección es obligatoria")
        String direccion,

        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad debe ser mayor a cero")
        Integer capacidad,

        @NotBlank(message = "La ciudad es obligatoria")
        String ciudad
) {}