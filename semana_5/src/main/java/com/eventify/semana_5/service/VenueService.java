package com.eventify.semana_5.service;

import com.eventify.semana_5.dto.VenueCreateDTO;
import com.eventify.semana_5.dto.VenueResponseDTO;
import com.eventify.semana_5.dto.mapper.VenueMapper;
import com.eventify.semana_5.exception.ResourceNotFoundException;
import com.eventify.semana_5.model.Venue;
import com.eventify.semana_5.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;

    @Transactional
    public VenueResponseDTO create(VenueCreateDTO dto) {
        if (dto.nombre() == null || dto.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la sede no puede estar vacío");
        }
        if (dto.capacidad() == null || dto.capacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a 0");
        }
        Venue venue = venueMapper.toEntity(dto);
        Venue savedVenue = venueRepository.save(venue);
        return venueMapper.toResponse(savedVenue);
    }

    @Transactional(readOnly = true)
    public List<VenueResponseDTO> findAll() {
        return venueRepository.findAll().stream()
                .map(venueMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<VenueResponseDTO> findAll(Pageable pageable) {
        return venueRepository.findAll(pageable)
                .map(venueMapper::toResponse);
    }

    // Metodo solicitado por VenueController para respuestas paginadas en DTO
    @Transactional(readOnly = true)
    public Page<VenueResponseDTO> findAllDTO(Pageable pageable) {
        return findAll(pageable);
    }

    @Transactional(readOnly = true)
    public VenueResponseDTO findByIdDTO(Long id) {
        Venue venue = findById(id);
        return venueMapper.toResponse(venue);
    }

    @Transactional(readOnly = true)
    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La sede con id: '" + id + "' no fue encontrada."
                ));
    }

    @Transactional
    public VenueResponseDTO update(Long id, VenueCreateDTO dto) {
        Venue venue = findById(id);
        venue.setNombre(dto.nombre());
        venue.setDireccion(dto.direccion());
        venue.setCapacidad(dto.capacidad());
        venue.setCiudad(dto.ciudad());
        Venue updatedVenue = venueRepository.save(venue);
        return venueMapper.toResponse(updatedVenue);
    }

    @Transactional
    public void delete(Long id) {
        Venue venue = findById(id);
        venueRepository.delete(venue);
    }
}