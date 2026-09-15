package com.eventify.semana_2.repository;

import com.eventify.semana_2.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(
                LocalDateTime.of(2026, 9, 8, 12, 30),
                savedEvent.getFecha()
        );
        assertEquals(
                "Conferencia sobre Java",
                savedEvent.getDescripcion()
        );
        assertNotNull(savedEvent.getId());
    }

    @Test
    void shouldFindEventById() {

        // El ID será generado por la base de datos.
        Event event = Event.builder()
                .nombre("Conferencia Java")
                .fecha(LocalDateTime.of(2026, 9, 8, 12, 30))
                .descripcion("Conferencia sobre Java")
                .build();

        Event savedEvent = eventRepository.save(event);

        // Buscamos el evento utilizando el ID generado.
        Optional<Event> result =
                eventRepository.findById(savedEvent.getId());

        // Comprobamos que encontramos el evento.
        assertTrue(result.isPresent());

        // Comprobamos que los datos recuperados son correctos.
        assertEquals("Conferencia Java", savedEvent.getNombre());
        assertEquals(
                LocalDateTime.of(2026, 9, 8, 12, 30),
                savedEvent.getFecha()
        );
        assertEquals(
                "Conferencia sobre Java",
                savedEvent.getDescripcion()
        );
        assertNotNull(savedEvent.getId());
    }

    @Test
    void shouldReturnEmptyWhenEventDoesNotExist() {

        // Buscamos un ID que no existe en la base de datos.
        Optional<Event> result =
                eventRepository.findById(9999L);

        // El Repository debe devolver un Optional vacío.
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindEventsByNombreIgnoringCase() {

        Event event1 = Event.builder()
                .nombre("Conferencia Java")
                .fecha(LocalDateTime.of(2026, 9, 8, 12, 30))
                .descripcion("Conferencia sobre Java")
                .build();

        Event event2 = Event.builder()
                .nombre("Taller Spring Boot")
                .fecha(LocalDateTime.of(2026, 9, 10, 15, 00))
                .descripcion("Taller sobre Spring Boot")
                .build();

        // se guardan ambos
        eventRepository.saveAll(List.of(event1, event2));

        // Buscamos utilizando "java"
        List<Event> result = eventRepository.findByNombreContainingIgnoreCase("JAVA");

        assertEquals(1, result.size());

        // Comprobamos que es el evento esperado.
        assertEquals("Conferencia Java", result.get(0).getNombre());
    }

    @Test
    void shouldReturnEventsWithPagination() {

        // Guardamos 10 eventos en la base de datos de prueba.
        for (int i = 0; i < 50; i++) {

            Event event = Event.builder()
                    .nombre("Evento " + i)
                    .fecha(LocalDateTime.of(2026, 9, 8, 12, 30))
                    .descripcion("Descripción del evento " + i)
                    .build();

            eventRepository.save(event);
        }

        // Solicitamos la primera página con un máximo de 5 eventos.
        Pageable pageable = PageRequest.of(0, 5);

        // Ejecutamos la consulta paginada real contra la BD.
        Page<Event> result = eventRepository.findAll(pageable);

        // La página debe contener exactamente 5 eventos.
        assertEquals(5, result.getContent().size());

        // En total existen 10 eventos.
        assertEquals(50, result.getTotalElements());

        // 50 eventos / 5 por página = 2 páginas.
        assertEquals(10, result.getTotalPages());

        // Estamos en la primera página.
        assertEquals(0, result.getNumber());

        // El tamaño solicitado era 5.
        assertEquals(5, result.getSize());
    }
}
