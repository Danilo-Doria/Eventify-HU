package com.eventify.semana_2.controller;

import com.eventify.semana_2.model.Venue;
import com.eventify.semana_2.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

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
            description = "Obtiene todos los lugares registrados en el catálogo de Eventify"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de lugares obtenida correctamente"
            )
    })
    @GetMapping
    public ResponseEntity<List<Venue>> findAll() {
        return ResponseEntity.ok(venueService.findAll());
    }
}
