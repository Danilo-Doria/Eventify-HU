package com.eventify.semana_4.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name ="events")

// Excluye automáticamente los eventos inactivos de las consultas ORM normales.
@SQLRestriction("active = true")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, length = 500)
    private String descripcion;

    // Si no se especifica otro valor al construir el objeto, comienza activo.
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    // Cada evento debe estar asociado obligatoriamente a un lugar.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    // Un evento puede tener varias categorías y una categoría puede pertenecer a varios eventos.
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "events_categories", joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
}