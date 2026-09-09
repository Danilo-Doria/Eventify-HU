package com.eventify.semana_1.service;

import com.eventify.semana_1.model.Event;
import com.eventify.semana_1.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

// Forma de crear test unitarios un poco "antigua"
public class EventServiceTest {

    // Esto basicamente le dice a mockito, que debe crear un objeto falso de EventRepository para pruebas
    @Mock
    private EventRepository eventRepository;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        eventService = new EventService(eventRepository);
    }

    @Test
    void shouldCreateEventWhenNameIsValid() {
        Event event = new Event(
                1L,
                "Conferencia",
                LocalDateTime.of(2026, 9, 8, 12, 30),
                "Conferecia sobre Java"
        );

        // Cuando service guarde un evento el mock devolvera dicho evento
        when(eventRepository.save(event)).thenReturn(event);

        // Ejecucion del service
        Event result = eventService.create(event);

        assertEquals(event, result);

        verify(eventRepository).save(event);
    }

    @Test
    void shouldRejectEventWhenNameIsEmpty() {
        Event event = new Event(
                1L,
                "",
                LocalDateTime.of(2026, 9, 8, 12, 30),
                "Evento invalido"
        );

        // Comprobando si eventService.create(event) lanza una IllegalArgumentException
        assertThrows(
                IllegalArgumentException.class, () -> eventService.create(event)
        );

        // Comprobamos que no se guardó
        verify(eventRepository, never()).save(event);
    }
}
