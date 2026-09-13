package com.eventify.semana_1.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    private Long id;
    private String nombre;
    private LocalDateTime fecha;
    private String descripcion;
}