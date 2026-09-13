package com.eventify.semana_1.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    private long id;
    private String nombre;
    private String dirección;
    private int capacidad;
}