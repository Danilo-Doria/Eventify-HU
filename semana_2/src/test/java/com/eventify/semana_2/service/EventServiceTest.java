package com.eventify.semana_2.service;

import com.eventify.semana_2.exception.ResourceNotFoundException;
import com.eventify.semana_2.model.Event;
import com.eventify.semana_2.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        Event event = Event.builder()
                .id(1L)
                .nombre("Conferencia")
                .fecha(LocalDateTime.of(2026, 9, 8, 12, 30))
                .descripcion("Conferecia sobre Java")
                .build();

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
    void shouldReturnAllEventsWithPagination() {
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

        // Creamos el Pageable que queremos simular:
        // página 0 y 2 elementos por página.
        Pageable pageable = PageRequest.of(0, 2);

        // Convertimos nuestra lista en un objeto Page.
        Page<Event> eventPage = new PageImpl<>(events);

        // Configuramos el comportamiento del mock.
        // Cuando el Repository reciba ese Pageable,
        // devolverá nuestra página simulada.
        when(eventRepository.findAll(pageable)).thenReturn(eventPage);

        // Ejecutamos el método del Service.
        Page<Event> result = eventService.findAll(pageable);

        // Comprobamos que el Service devuelve la página esperada.
        assertEquals(eventPage, result);

        // Comprobamos que el Repository fue llamado correctamente.
        verify(eventRepository).findAll(pageable);
    }

    @Test
    void shouldReturnEventWhenIdExists() {

        Event event = new Event(
                1L,
                "Conferencia Java",
                LocalDateTime.of(2026, 9, 8, 12, 30),
                "Conferencia sobre Java"
        );

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        // Act: ejecutamos el metodo del Service
        Event result = eventService.findById(1L);

        assertEquals(event, result);

        verify(eventRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenEventIdDoesNotExist() {

        // Configuramos el mock para simular que el evento no existe.
        // Optional.empty() representa que no se encontró ningún evento.
        when(eventRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Comprobamos que el Service lance nuestra excepción personalizada.
        assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.findById(999L)
        );

        // Comprobamos que el Repository haya sido consultado con el ID correcto.
        verify(eventRepository).findById(999L);
    }

    @Test
    void shouldUpdateEventWhenIdExists() {
        // Datos existentes
        Event existingEvent = new Event(
                1L,
                "Conferencia Java",
                LocalDateTime.of(2026, 9, 8, 12, 30),
                "Conferencia sobre Java"
        );

        // Nuevos datos
        Event updatedEvent = new Event(
                null,
                "Conferencia Spring Boot",
                LocalDateTime.of(2026, 9, 10, 15, 00),
                "Conferencia sobre Spring Boot"
        );

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(existingEvent));

        // Simulamos el save().
        // Cuando el Service guarde el evento actualizado,
        // el mock devolverá ese mismo evento.
        when(eventRepository.save(existingEvent))
                .thenReturn(existingEvent);

        // Ejecutamos el metodo que estamos probando.
        Event result = eventService.update(1L, updatedEvent);

        // Comprobamos que los datos hayan sido actualizados.
        assertEquals(1L, result.getId());
        assertEquals("Conferencia Spring Boot", result.getNombre());
        assertEquals(
                LocalDateTime.of(2026, 9, 10, 15, 00),
                result.getFecha()
        );
        assertEquals(
                "Conferencia sobre Spring Boot",
                result.getDescripcion()
        );

        // Comprobamos que primero se buscó el evento por ID.
        verify(eventRepository).findById(1L);

        // Comprobamos que posteriormente se guardó el evento actualizado.
        verify(eventRepository).save(existingEvent);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingEvent() {

        // Datos que intentaríamos utilizar para actualizar.
        Event updatedEvent = new Event(
                null,
                "Conferencia Spring Boot",
                LocalDateTime.of(2026, 9, 10, 15, 00),
                "Conferencia sobre Spring Boot"
        );

        // Simulamos que el Repository NO encuentra el evento.
        when(eventRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Comprobamos que el Service lance la excepción.
        assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.update(999L, updatedEvent)
        );

        // Verificamos que se haya buscado el ID correcto.
        verify(eventRepository).findById(999L);

        // Como el evento no existe, nunca deberia ejecutarse save().
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldDeleteEventWhenIdExists() {

        Event event = new Event(
                1L,
                "Conferencia Java",
                LocalDateTime.of(2026, 9, 8, 12, 30),
                "Conferencia sobre Java"
        );

        // Simulamos que el evento existe.
        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        // Ejecutamos el metodo que estamos probando.
        eventService.delete(1L);

        // Verificamos que primero se haya comprobado que existe.
        verify(eventRepository).findById(1L);

        // Verificamos que posteriormente se haya eliminado.
        verify(eventRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingEvent() {

        // Simulamos que el evento no existe.
        when(eventRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Esperamos que el Service lance ResourceNotFoundException.
        assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.delete(999L)
        );

        // Verificamos que se haya buscado el ID.
        verify(eventRepository).findById(999L);

        // Como el evento no existe, nunca debe ejecutarse deleteById().
        verify(eventRepository, never()).deleteById(999L);
    }
}
