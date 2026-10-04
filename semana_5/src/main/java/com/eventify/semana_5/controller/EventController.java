package com.eventify.semana_5.controller;

import com.eventify.semana_5.dto.EventCreateDTO;
import com.eventify.semana_5.dto.EventResponseDTO;
import com.eventify.semana_5.dto.EventSummaryDTO;
import com.eventify.semana_5.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(
            summary = "Registrar un evento",
            description = "Crea un nuevo evento asociando su Venue y Categorías a partir de sus identificadores"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "El Venue o alguna Categoría no existe")
    })
    @PostMapping
    public ResponseEntity<EventResponseDTO> create(@Valid @RequestBody EventCreateDTO dto) {
        EventResponseDTO createdEvent = eventService.create(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEvent.id())
                .toUri();

        return ResponseEntity.created(location).body(createdEvent);
    }

    @Operation(
            summary = "Consultar listado optimizado de eventos",
            description = "Retorna una proyección liviana (EventSummaryDTO) optimizada con Slice para listados masivos"
    )
    @GetMapping
    public ResponseEntity<Slice<EventSummaryDTO>> findAll(
            @ParameterObject
            @PageableDefault(size = 10, sort = "fecha", direction = Sort.Direction.ASC)
            Pageable pageable) {

        return ResponseEntity.ok(eventService.findAllSummary(pageable));
    }

    @Operation(
            summary = "Consultar detalle de un evento por ID",
            description = "Retorna el detalle completo de un evento individual utilizando EventResponseDTO"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findByIdDTO(id));
    }

    @Operation(
            summary = "Actualizar un evento",
            description = "Actualiza los datos de un evento existente por su ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Evento o relaciones no encontradas")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EventCreateDTO dto) {
        return ResponseEntity.ok(eventService.update(id, dto));
    }

    @Operation(
            summary = "Eliminar un evento",
            description = "Elimina un evento del catálogo por su ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Evento eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}