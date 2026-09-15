package com.eventify.semana_2.controller;

import com.eventify.semana_2.model.Venue;
import com.eventify.semana_2.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @Operation(
            summary = "Registrar un lugar",
            description = "Registra un nuevo lugar en el catálogo de Eventify"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Lugar creado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El nombre del lugar es obligatorio"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "La capacidad debe ser mayor a cero"
            )
    })
    @PostMapping
    public ResponseEntity<Venue> create(@RequestBody Venue venue) {
        Venue createdVenue = venueService.create(venue);

        // ServletUriComponentsBuilder toma la URL actual de la petición (ej. "http://localhost:8080/api/venues"),
        // le añade el path "/{id}" y reemplaza el parámetro con el ID del nuevo objeto.
        // Resultado de 'location': "http://localhost:8080/api/venues/1"
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdVenue.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdVenue);
    }

    @Operation(
            summary = "Consultar lugares",
            description = "Obtiene los lugares registrados en el catálogo de Eventify mediante paginación y ordenamiento"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lugares obtenidos correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<Page<Venue>> findAll(
            @ParameterObject
            @PageableDefault(
                    size = 10,
                    sort = "nombre",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable) {

        return ResponseEntity.ok(venueService.findAll(pageable));
    }

    @Operation(
            summary = "Consultar un lugar por ID",
            description = "Obtiene un lugar específico mediante su identificador"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lugar encontrado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El lugar no existe"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Venue> findById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.findById(id));
    }

    @Operation(
            summary = "Actualizar un lugar",
            description = "Actualiza un lugar mediante su identificador"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lugar actualizado correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El nombre del lugar es obligatorio"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El lugar no existe"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Venue> update(@PathVariable Long id, @RequestBody Venue venue) {
        return ResponseEntity.ok(venueService.update(id, venue));
    }

    @Operation(
            summary = "Eliminar un lugar",
            description = "Elimina un lugar físicamente mediante su identificador"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Lugar eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El lugar no existe"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
