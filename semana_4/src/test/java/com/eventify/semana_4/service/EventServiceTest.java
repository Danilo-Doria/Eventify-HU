package com.eventify.semana_4.service;

import com.eventify.semana_4.exception.ResourceNotFoundException;
import com.eventify.semana_4.model.Event;
import com.eventify.semana_4.model.Venue;
import com.eventify.semana_4.repository.CategoryRepository;
import com.eventify.semana_4.repository.EventRepository;
import com.eventify.semana_4.repository.VenueRepository;
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

    @Mock
    private EventRepository eventRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        // Se crea manualmente el Service y se inyectan los tres repositorios falsos.
        eventService = new EventService(
                eventRepository,
                venueRepository,
                categoryRepository
        );
    }

    @Test
    void shouldCreateEventWhenNameIsValid() {

        Venue venue = Venue.builder()
                .id(1L)
                .nombre("Centro de Convenciones")
                .direccion("Calle 10")
                .capacidad(500)
                .ciudad("Barranquilla")
                .build();

        Event event = Event.builder()
                .id(1L)
                .nombre("Conferencia")
                .fecha(LocalDateTime.of(2026, 9, 8, 12, 30))
                .descripcion("Conferencia sobre Java")
                .active(true)
                .venue(venue)
                .build();

        // Simulamos que el Venue existe en la base de datos.
        when(venueRepository.findById(1L))
                .thenReturn(Optional.of(venue));

        // Simulamos que el EventRepository guarda correctamente el evento.
        when(eventRepository.save(event))
                .thenReturn(event);

        // Ejecutamos el metodo del Service.
        Event result = eventService.create(event);

        // Comprobamos que se devuelve el evento creado.
        assertEquals(event, result);

        // Comprobamos que el Venue fue buscado.
        verify(venueRepository).findById(1L);

        // Comprobamos que el evento fue guardado.
        verify(eventRepository).save(event);
    }

    @Test
    void shouldRejectEventWhenNameIsEmpty() {

        Venue venue = Venue.builder()
                .id(1L)
                .nombre("Centro de Convenciones")
                .direccion("Calle 10")
                .capacidad(500)
                .ciudad("Barranquilla")
                .build();

        Event event = Event.builder()
                .id(1L)
                .nombre("")
                .fecha(LocalDateTime.of(2026, 9, 8, 12, 30))
                .descripcion("Evento invalido")
                .venue(venue)
                .build();

        assertThrows(IllegalArgumentException.class, () -> eventService.create(event));

        // Comprobamos que nunca se intentó guardar el evento.
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldReturnAllEventsWithPagination() {

        Venue venue = Venue.builder()
                .id(1L)
                .nombre("Centro de Convenciones")
                .direccion("Calle 10")
                .capacidad(500)
                .ciudad("Barranquilla")
                .build();

        List<Event> events = List.of(
                Event.builder()
                        .id(1L)
                        .nombre("Conferencia")
                        .fecha(LocalDateTime.of(2026, 9, 8, 12, 30))
                        .descripcion("Conferencia sobre Java")
                        .active(true)
                        .venue(venue)
                        .build(),

                Event.builder()
                        .id(2L)
                        .nombre("Conferencia")
                        .fecha(LocalDateTime.of(2026, 10, 11, 8, 45))
                        .descripcion("Conferencia sobre Angular")
                        .active(true)
                        .venue(venue)
                        .build()
        );

        // Creamos el Pageable que queremos simular:
        // página 0 y 2 elementos por página.
        Pageable pageable = PageRequest.of(0, 2);

        // Convertimos nuestra lista en un objeto Page.
        Page<Event> eventPage = new PageImpl<>(events);

        // Configuramos el comportamiento del mock.
        // Cuando el Repository reciba ese Pageable,
        // devolverá nuestra página simulada.
        when(eventRepository.findAll(pageable)).thenReturn(eventPage);

        // Ejecutamos el metodo del Service.
        Page<Event> result = eventService.findAll(pageable);

        // Comprobamos que el Service devuelve la página esperada.
        assertEquals(eventPage, result);

        // Comprobamos que el Repository fue llamado correctamente.
        verify(eventRepository).findAll(pageable);
    }

    @Test
    void shouldReturnEventWhenIdExists() {

        Event event = Event.builder()
                .id(1L)
                .nombre("Conferencia Java")
                .fecha(LocalDateTime.of(2026, 9, 8, 12, 30))
                .descripcion("Conferencia sobre Java")
                .active(true)
                .build();

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

        Venue venue = Venue.builder()
                .id(1L)
                .nombre("Centro de Convenciones")
                .direccion("Calle 10")
                .capacidad(500)
                .ciudad("Barranquilla")
                .build();

        // Datos existentes
        Event existingEvent = Event.builder()
                .id(1L)
                .nombre("Conferencia Java")
                .fecha(LocalDateTime.of(2026, 9, 8, 12, 30))
                .descripcion("Conferencia sobre Java")
                .active(true)
                .venue(venue)
                .build();

        // Nuevos datos
        Event updatedEvent = Event.builder()
                .nombre("Conferencia Spring Boot")
                .fecha(LocalDateTime.of(2026, 9, 10, 15, 00))
                .descripcion("Conferencia sobre Spring Boot")
                .venue(venue)
                .build();

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(existingEvent));

        // Simulamos que el Venue existe en la base de datos.
        when(venueRepository.findById(1L))
                .thenReturn(Optional.of(venue));

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
        Event updatedEvent = Event.builder()
                .nombre("Conferencia Spring Boot")
                .fecha(LocalDateTime.of(2026, 9, 10, 15, 00))
                .descripcion("Conferencia sobre Spring Boot")
                .build();

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

        Event event = Event.builder()
                .id(1L)
                .nombre("Conferencia Java")
                .fecha(LocalDateTime.of(2026, 9, 8, 12, 30))
                .descripcion("Conferencia sobre Java")
                .active(true)
                .build();

        // Simulamos que el evento existe.
        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        // Simulamos el save() que realiza el borrado lógico.
        when(eventRepository.save(event))
                .thenReturn(event);

        // Ejecutamos el metodo que estamos probando.
        eventService.delete(1L);

        // Verificamos que primero se haya comprobado que existe.
        verify(eventRepository).findById(1L);

        // Comprobamos que el evento haya sido marcado como inactivo.
        assertEquals(false, event.getActive());

        // Verificamos que posteriormente se haya guardado el cambio.
        verify(eventRepository).save(event);

        // El evento no debe eliminarse físicamente de la base de datos.
        verify(eventRepository, never()).deleteById(1L);
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