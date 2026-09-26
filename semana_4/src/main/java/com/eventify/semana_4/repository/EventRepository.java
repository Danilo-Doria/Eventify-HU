package com.eventify.semana_4.repository;

import com.eventify.semana_4.model.Event;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByNombreContainingIgnoreCase(String nombre);

    List<Event> findByVenueCiudadContainingIgnoreCase(String ciudad);

    // Busca eventos cuya fecha esté dentro del rango indicado.
    List<Event> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Obtiene todos los eventos ordenados desde la fecha más reciente hasta la más antigua.
    List<Event> findAllByOrderByFechaDesc();

    // Obtiene eventos paginados sin calcular el total de registros.
    Slice<Event> findAllByOrderByFechaDesc(Pageable pageable);

    // Busca eventos por ciudad y los ordena desde la fecha más reciente.
    Slice<Event> findByVenueCiudadContainingIgnoreCaseOrderByFechaDesc(String ciudad, Pageable pageable);

    // Busca eventos asociados a una categoría por su nombre.
    @Query("""
            SELECT DISTINCT e
            FROM Event e
            JOIN e.categories c
            WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
            ORDER BY e.fecha DESC
            """)
    Slice<Event> findByCategoriaNombreContainingIgnoreCase(
            @Param("nombre") String nombre,
            Pageable pageable
    );

    // Carga el Venue junto con cada Event para evitar consultas adicionales al acceder al lugar.
    @EntityGraph(attributePaths = {"venue"})
    @Query("""
            SELECT e
            FROM Event e
            ORDER BY e.fecha DESC
            """)
    Slice<Event> findAllWithVenue(Pageable pageable);

    // Busca eventos cuyo Venue tenga una capacidad igual o superior a la indicada.
    Slice<Event> findByVenueCapacidadGreaterThanEqualOrderByFechaDesc( Integer capacidad, Pageable pageable );
}
