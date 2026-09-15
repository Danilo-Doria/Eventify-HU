package com.eventify.semana_2.repository;

import com.eventify.semana_2.model.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class VenueRepositoryTest {
    @Autowired
    private VenueRepository venueRepository;

    @Test
    void shouldSaveVenue() {

        Venue venue = Venue.builder()
                .nombre("Plaza las Americas")
                .direccion("Mz B Lote 5, Barrio Mango Azul")
                .capacidad(150)
                .build();

        Venue savedVenue = venueRepository.save(venue);

        assertNotNull(savedVenue.getId());
        assertEquals("Plaza las Americas", savedVenue.getNombre());
        assertEquals("Mz B Lote 5, Barrio Mango Azul", savedVenue.getDireccion());
        assertEquals(150, savedVenue.getCapacidad());
    }

    @Test
    void shouldFindVenueById() {

        Venue venue = Venue.builder()
                .nombre("Plaza las Americas")
                .direccion("Mz B Lote 5, Barrio Mango Azul")
                .capacidad(150)
                .build();

        Venue savedVenue = venueRepository.save(venue);

        Optional<Venue> result =
                venueRepository.findById(savedVenue.getId());

        assertTrue(result.isPresent());
        assertEquals("Plaza las Americas", result.get().getNombre());
    }

    @Test
    void shouldReturnEmptyWhenVenueDoesNotExist() {

        Optional<Venue> result =
                venueRepository.findById(9999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnVenuesWithPagination() {

        for (int i = 1; i <= 10; i++) {

            Venue venue = Venue.builder()
                    .nombre("Venue " + i)
                    .direccion("Dirección " + i)
                    .capacidad(100 + i)
                    .build();

            venueRepository.save(venue);
        }

        Pageable pageable = PageRequest.of(0, 5);

        Page<Venue> result =
                venueRepository.findAll(pageable);

        assertEquals(5, result.getContent().size());
        assertEquals(10, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertEquals(5, result.getSize());
    }
}
