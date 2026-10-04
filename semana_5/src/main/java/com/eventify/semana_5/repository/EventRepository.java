package com.eventify.semana_5.repository;

import com.eventify.semana_5.dto.EventSummaryDTO;
import com.eventify.semana_5.model.Event;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByNombreContainingIgnoreCase(String nombre);

    List<Event> findByVenueCiudadContainingIgnoreCase(String ciudad);

    // Busca eventos cuya fecha esté dentro del rango indicado.
    List<Event> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Obtiene eventos paginados sin calcular el total de registros.
    Slice<Event> findAllByOrderByFechaDesc(Pageable pageable);

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
    Slice<Event> findByVenueCapacidadGreaterThanEqualOrderByFechaDesc(
            Integer capacidad,
            Pageable pageable
    );

    // Devuelve un resumen paginado de eventos, consultando solo los campos necesarios.
    @Query("""
        SELECT new com.eventify.semana_5.dto.EventSummaryDTO(
            e.nombre,
            e.fecha,
            v.nombre,
            v.ciudad
        )
        FROM Event e
        JOIN e.venue v
        """)
    Slice<EventSummaryDTO> findEventSummaries(Pageable pageable);

    // Busca eventos aplicando opcionalmente ciudad, categoría, capacidad y rango de fechas.
    // Al devolver un DTO y un Slice, evita cargar entidades completas y evita calcular el total.
    @Query("""
        SELECT DISTINCT new com.eventify.semana_5.dto.EventSummaryDTO(
            e.nombre,
            e.fecha,
            v.nombre,
            v.ciudad
        )
        FROM Event e
        JOIN e.venue v
        LEFT JOIN e.categories c
        WHERE
            (:ciudad IS NULL OR :ciudad = '' OR LOWER(v.ciudad) LIKE LOWER(CONCAT('%', :ciudad, '%')))
        AND
            (:categoria IS NULL OR :categoria = '' OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :categoria, '%')))
        AND
            (:capacidad IS NULL OR v.capacidad >= :capacidad)
        AND
            (:fechaInicio IS NULL OR e.fecha >= :fechaInicio)
        AND
            (:fechaFin IS NULL OR e.fecha <= :fechaFin)
        """)
    Slice<EventSummaryDTO> findEventSummariesWithFilters(
            @Param("ciudad") String ciudad,
            @Param("categoria") String categoria,
            @Param("capacidad") Integer capacidad,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );
}
