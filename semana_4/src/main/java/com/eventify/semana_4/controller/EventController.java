package com.eventify.semana_4.controller;

import com.eventify.semana_4.dto.EventSummaryDTO;
import com.eventify.semana_4.model.Event;
import com.eventify.semana_4.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @Operation(
            summary = "Registrar un evento",
            description = "Registra un nuevo evento en el catálogo de Eventify"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Evento creado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El nombre del evento es obligatorio"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El Venue o alguna de las categorías indicadas no existe"
            )
    })
    @PostMapping
    public ResponseEntity<Event> create(@RequestBody Event event) {
        Event createdEvent = eventService.create(event);

        // ServletUriComponentsBuilder toma la URL actual de la petición (ej. "http://localhost:8080/api/events"),
        // le añade el path "/{id}" y reemplaza el parámetro con el ID del nuevo objeto.
        // Resultado de 'location': "http://localhost:8080/api/events/1"
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEvent.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdEvent);
    }

    @Operation(
            summary = "Consultar eventos activos",
            description = """
                    Obtiene los eventos activos del catálogo.
                    Los resultados se ordenan por fecha de forma descendente y utilizan
                    paginación mediante Slice, evitando calcular el total de registros.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Eventos obtenidos correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<Slice<Event>> findAll(
            @ParameterObject
            @PageableDefault(
                    size = 10,
                    sort = "fecha",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {

        return ResponseEntity.ok(eventService.findAllByFechaDesc(pageable));
    }

    @Operation(
            summary = "Consultar eventos por nombre",
            description = "Obtiene eventos cuyo nombre contiene el texto indicado, ignorando mayúsculas y minúsculas."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Eventos encontrados correctamente"
            )
    })
    @GetMapping("/search")
    public ResponseEntity<List<Event>> findByNombre(

            @Parameter(
                    description = "Texto parcial del nombre del evento",
                    example = "concierto"
            )
            @RequestParam String nombre) {

        return ResponseEntity.ok(eventService.findByNombreContaining(nombre));
    }

    @Operation(
            summary = "Consultar un evento por ID",
            description = "Obtiene un evento específico mediante su identificador"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Evento encontrado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El evento no existe"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Event> findById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @Operation(
            summary = "Consultar eventos por ciudad",
            description = "Obtiene eventos cuya ciudad contiene el texto indicado, ignorando mayúsculas y minúsculas"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Eventos encontrados correctamente"
            )
    })
    @GetMapping("/search/city")
    public ResponseEntity<List<Event>> findByCiudad(
            @Parameter(description = "Texto parcial de la ciudad a buscar")
            @RequestParam String ciudad) {

        return ResponseEntity.ok(eventService.findByCiudad(ciudad));
    }

    @Operation(
            summary = "Consultar eventos por rango de fechas",
            description = "Obtiene eventos cuya fecha se encuentra entre la fecha inicial y la fecha final"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Eventos encontrados correctamente"
            )
    })
    @GetMapping("/search/date")
    public ResponseEntity<List<Event>> findByFechaBetween(
            @Parameter(description = "Fecha y hora inicial del rango")
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicio,

            @Parameter(description = "Fecha y hora final del rango")
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin) {

        return ResponseEntity.ok(eventService.findByFechaBetween(fechaInicio, fechaFin));
    }

    @Operation(
            summary = "Consultar eventos por capacidad",
            description = "Obtiene eventos realizados en lugares cuya capacidad es igual o superior a la indicada"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Eventos encontrados correctamente"
            )
    })
    @GetMapping("/search/capacity")
    public ResponseEntity<Slice<Event>> findByCapacidad(
            @Parameter(description = "Capacidad mínima del lugar")
            @RequestParam Integer capacidad,

            @ParameterObject Pageable pageable) {

        return ResponseEntity.ok(eventService.findByCapacidad(capacidad, pageable));
    }

    @Operation(
            summary = "Consultar eventos por categoría",
            description = "Obtiene eventos asociados a categorías cuyo nombre contiene el texto indicado, ignorando mayúsculas y minúsculas"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Eventos encontrados correctamente"
            )
    })
    @GetMapping("/search/category")
    public ResponseEntity<Slice<Event>> findByCategoria(
            @Parameter(description = "Nombre o texto parcial de la categoría")
            @RequestParam String nombre,

            @ParameterObject Pageable pageable) {

        return ResponseEntity.ok(eventService.findByCategoria(nombre, pageable));
    }

    @Operation(
            summary = "Consultar resumen de eventos",
            description = "Obtiene un listado optimizado de eventos utilizando una proyección con los datos principales del evento y su Venue"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resúmenes de eventos obtenidos correctamente"
            )
    })
    @GetMapping("/summary")
    public ResponseEntity<Slice<EventSummaryDTO>> findEventSummaries(
            @ParameterObject
            @PageableDefault(
                    size = 10,
                    sort = "fecha",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {

        return ResponseEntity.ok(eventService.findEventSummaries(pageable));
    }

    @Operation(
            summary = "Actualizar un evento",
            description = "Actualiza un evento mediante su identificador"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Evento actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El nombre del evento es obligatorio"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El evento no existe"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable Long id, @RequestBody Event event) {
        return ResponseEntity.ok(eventService.update(id, event));
    }

    @Operation(
            summary = "Eliminar un evento",
            description = "Desactiva lógicamente un evento mediante su identificador. El registro se conserva en la base de datos."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Evento desactivado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El evento no existe o ya está inactivo"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}