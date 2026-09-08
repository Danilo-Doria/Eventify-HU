package com.eventify.semana_1.controller;

import com.eventify.semana_1.model.Event;
import com.eventify.semana_1.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

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
        return ResponseEntity.status(201).body(createdEvent);
    }

    @Operation(
            summary = "Consultar eventos",
            description = "Obtiene todos los eventos registrados en el catálogo de Eventify"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de eventos obtenida correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<List<Event>> findAll() {
        return ResponseEntity.ok(eventService.findAll());
    }
}