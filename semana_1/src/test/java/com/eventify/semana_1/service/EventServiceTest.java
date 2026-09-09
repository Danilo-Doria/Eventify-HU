package com.eventify.semana_1.service;

import com.eventify.semana_1.model.Event;
import com.eventify.semana_1.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/*
 * @ExtendWith(MockitoExtension.class)
 * Esto le indica a Junit 5 que de be usar la extension de mockito
 * Con esto Mockito incializa automaticamente los @Mocks
 */
@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    // Esto basicamente le dice a mockito, que debe crear un objeto falso de EventRepository para pruebas
    @Mock
    private EventRepository eventRepository;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        // Se crea manualmente el service y se inyecta el repositorio falso
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

    @Test
    void shouldReturnAllEvents() {
        List<Event> events = List.of(
                new Event(
                        1L,
                        "Conferencia",
                        LocalDateTime.of(2026, 9, 8, 12, 30),
                        "Conferecia sobre Java"
                ), new Event(
                        2L,
                        "Conferencia",
                        LocalDateTime.of(2026, 10, 11, 8, 45),
                        "Conferecia sobre Angular"
                ));

        // Configurar el comportamiento del mock
        // EL mock devolverá el listado de events
        when(eventRepository.findAll()).thenReturn(events);

        List<Event> result = eventService.findAll();

        // Assert, comprueaba que el service devuelva el venue esperado
        assertEquals(events, result);

        // Verify, comprueba que el servicio llamó al metodo findAll() del repository
        verify(eventRepository).findAll();
    }
}
