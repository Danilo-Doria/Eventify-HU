package com.eventify.semana_1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    private long id;
    private String nombre;
    private String dirección;
    private int capacidad;
}