package com.eventify.semana_1.config;

import com.eventify.semana_1.model.Event;
import com.eventify.semana_1.model.Venue;
import com.eventify.semana_1.repository.EventRepository;
import com.eventify.semana_1.repository.VenueRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    public Event initialEvent(EventRepository eventRepository) {

        Event event = new Event(
                1L,
                "Evento de prueba",
                LocalDateTime.of(2026, 10, 15, 19, 30),
                "Evento inicial de Eventify"
        );

        return eventRepository.save(event);
    }

    @Bean
    public Venue initialVenue(VenueRepository venueRepository) {

        Venue venue = new Venue(
                1L,
                "Centro de Convenciones",
                "Calle 50 # 10-20",
                500
        );

        return venueRepository.save(venue);
    }
}