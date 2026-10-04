package com.eventify.semana_5.controller;

import com.eventify.semana_5.dto.VenueCreateDTO;
import com.eventify.semana_5.dto.VenueResponseDTO;
import com.eventify.semana_5.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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

    @Operation(summary = "Registrar una sede/venue")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sede creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<VenueResponseDTO> create(@Valid @RequestBody VenueCreateDTO dto) {
        VenueResponseDTO createdVenue = venueService.create(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdVenue.id())
                .toUri();

        return ResponseEntity.created(location).body(createdVenue);
    }

    @Operation(summary = "Listar todas las sedes paginadas")
    @GetMapping
    public ResponseEntity<Page<VenueResponseDTO>> findAll(
            @ParameterObject
            @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC)
            Pageable pageable) {

        return ResponseEntity.ok(venueService.findAllDTO(pageable));
    }

    @Operation(summary = "Consultar una sede por ID")
    @GetMapping("/{id}")
    public ResponseEntity<VenueResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.findByIdDTO(id));
    }

    @Operation(summary = "Actualizar una sede")
    @PutMapping("/{id}")
    public ResponseEntity<VenueResponseDTO> update(@PathVariable Long id, @Valid @RequestBody VenueCreateDTO dto) {
        return ResponseEntity.ok(venueService.update(id, dto));
    }

    @Operation(summary = "Eliminar una sede")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}