package com.eventify.semana_2.repository;

import com.eventify.semana_2.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class EventRepositoryTest {
    @Autowired
    private EventRepository eventRepository;

    @Test
    void shouldSaveEvent() {

        Event event = Event.builder()
                .nombre("Conferencia Java")
                .fecha(LocalDateTime.of(2026, 9, 8, 12, 30))
                .descripcion("Conferencia sobre Java")
                .build();

        Event savedEvent = eventRepository.save(event);

        assertNotNull(savedEvent.getId());
        assertEquals("Conferencia Java", savedEvent.getNombre());
    }
}
