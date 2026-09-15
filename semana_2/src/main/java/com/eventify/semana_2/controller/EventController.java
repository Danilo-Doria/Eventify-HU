package com.eventify.semana_2.controller;

import com.eventify.semana_2.model.Event;
import com.eventify.semana_2.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
            summary = "Consultar eventos",
            description = "Obtiene los eventos registrados en el catálogo de Eventify mediante paginación y ordenamiento"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Eventos obtenidos correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<Page<Event>> findAll(
            @PageableDefault(
                    size = 10,
                    sort = "nombre",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable) {

        return ResponseEntity.ok(eventService.findAll(pageable));
    }

    ////
    @Operation(
            summary = "Consultar eventos por nombre",
            description = "Obtiene una lista de eventos cuyo nombre contiene el texto de búsqueda"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Eventos obtenidos correctamente"
            )
    })
    @GetMapping("/search")
    public ResponseEntity<List<Event>> findByNombre(
            @RequestParam String nombre) {

        return ResponseEntity.ok(
                eventService.findByNombreContaining(nombre)
        );
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
            description = "Elimina un evento mediante su identificador"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Evento eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El evento no existe"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}