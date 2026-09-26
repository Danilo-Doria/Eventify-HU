package com.eventify.semana_4.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de la categoría, por ejemplo "Conciertos" o "Deportes".
    @Column(nullable = false, length = 100)
    private String nombre;

    // Descripción que explica el tipo de eventos de esta categoría.
    @Column(length = 500)
    private String descripcion;
}
