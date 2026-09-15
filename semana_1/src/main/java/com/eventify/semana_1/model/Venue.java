package com.eventify.semana_1.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue {
    private Long id;
    private String nombre;
    private String direccion;
    private Integer capacidad;
}