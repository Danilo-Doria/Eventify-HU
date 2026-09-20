package com.eventify.semana_3.service;

import com.eventify.semana_3.exception.ResourceNotFoundException;
import com.eventify.semana_3.model.Venue;
import com.eventify.semana_3.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;

    public Venue create(Venue venue) {
        if (venue.getNombre() == null || venue.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (venue.getCapacidad() == null || venue.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a cero");
        }

        return venueRepository.save(venue);
    }

    public Page<Venue> findAll(Pageable pageable) {
        return venueRepository.findAll(pageable);
    }

    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "El lugar con id: '" + id + "' no fue encontrado."
                        )
                );
    }

    public Venue update(Long id, Venue venue) {
        Venue existingVenue = venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El lugar con id: '" + id + "' no fue encontrado."));

        if (venue.getNombre() == null || venue.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (venue.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a cero");
        }

        existingVenue.setNombre(venue.getNombre());
        existingVenue.setDireccion(venue.getDireccion());
        existingVenue.setCapacidad(venue.getCapacidad());

        return venueRepository.save(existingVenue);
    }

    public void delete(Long id) {
        venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El lugar con id: '" + id + "' no fue encontrado."));

        venueRepository.deleteById(id);
    }
}
