package com.eventify.semana_2.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue {
    private long id;
    private String nombre;
    private String direccion;
    private int capacidad;
}