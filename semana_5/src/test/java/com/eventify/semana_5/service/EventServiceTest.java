package com.eventify.semana_5.service;

import com.eventify.semana_5.dto.EventCreateDTO;
import com.eventify.semana_5.dto.EventResponseDTO;
import com.eventify.semana_5.dto.EventSummaryDTO;
import com.eventify.semana_5.dto.mapper.EventMapper;
import com.eventify.semana_5.exception.ResourceNotFoundException;
import com.eventify.semana_5.model.Event;
import com.eventify.semana_5.model.Venue;
import com.eventify.semana_5.repository.CategoryRepository;
import com.eventify.semana_5.repository.EventRepository;
import com.eventify.semana_5.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private EventMapper eventMapper;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        // Se crea manualmente el Service y se inyectan los tres repositorios falsos y el mapper.
        eventService = new EventService(
                eventRepository,
                venueRepository,
                categoryRepository,
                eventMapper
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

        EventCreateDTO createDTO = new EventCreateDTO(
                "Conferencia",
                LocalDateTime.of(2026, 9, 8, 12, 30),
                "Conferencia sobre Java",
                1L,
                Set.of()
        );

        Event event = Event.builder()
                .id(1L)
                .nombre("Conferencia")
                .fecha(LocalDateTime.of(2026, 9, 8, 12, 30))
                .descripcion("Conferencia sobre Java")
                .active(true)
                .venue(venue)
                .build();

        EventResponseDTO responseDTO = new EventResponseDTO(
                1L,
                "Conferencia",
                LocalDateTime.of(2026, 9, 8, 12, 30),
                "Conferencia sobre Java",
                "Centro de Convenciones",
                Set.of()
        );

        when(eventMapper.toEntity(createDTO)).thenReturn(event);

        // Simulamos que el Venue existe en la base de datos.
        when(venueRepository.findById(1L))
                .thenReturn(Optional.of(venue));

        // Simulamos que el EventRepository guarda correctamente el evento.
        when(eventRepository.save(event))
                .thenReturn(event);

        when(eventMapper.toResponse(event)).thenReturn(responseDTO);

        // Ejecutamos el metodo del Service.
        EventResponseDTO result = eventService.create(createDTO);

        // Comprobamos que se devuelve el evento creado.
        assertEquals(responseDTO, result);

        // Comprobamos que el Venue fue buscado.
        verify(venueRepository).findById(1L);

        // Comprobamos que el evento fue guardado.
        verify(eventRepository).save(event);
    }

    @Test
    void shouldRejectEventWhenNameIsEmpty() {

        EventCreateDTO createDTO = new EventCreateDTO(
                "",
                LocalDateTime.of(2026, 9, 8, 12, 30),
                "Evento invalido",
                1L,
                Set.of()
        );

        assertThrows(IllegalArgumentException.class, () -> eventService.create(createDTO));

        // Comprobamos que nunca se intentó guardar el evento.
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldReturnAllEventsWithPagination() {

        // Creamos instancias simuladas de EventSummaryDTO
        EventSummaryDTO summary1 = mock(EventSummaryDTO.class);
        EventSummaryDTO summary2 = mock(EventSummaryDTO.class);

        List<EventSummaryDTO> summaryList = List.of(summary1, summary2);

        // Creamos el Pageable que queremos simular: página 0 y 2 elementos por página.
        Pageable pageable = PageRequest.of(0, 2);

        // Convertimos nuestra lista en un objeto Slice.
        Slice<EventSummaryDTO> expectedSlice = new SliceImpl<>(summaryList, pageable, false);

        // Configuramos el comportamiento del mock.
        // Cuando el Repository reciba ese Pageable en findEventSummaries,
        // devolverá nuestro Slice simulado.
        when(eventRepository.findEventSummaries(pageable)).thenReturn(expectedSlice);

        // Ejecutamos el método actual del Service: findAllSummary.
        Slice<EventSummaryDTO> result = eventService.findAllSummary(pageable);

        // Comprobamos que el Service devuelve el Slice esperado.
        assertEquals(expectedSlice, result);

        // Comprobamos que el Repository fue llamado correctamente en findEventSummaries.
        verify(eventRepository).findEventSummaries(pageable);
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
        // 1. ARRANGE
        Long eventId = 1L;
        Long venueId = 10L;

        Venue venue = Venue.builder()
                .id(venueId)
                .nombre("Teatro Municipal")
                .build();

        Event existingEvent = Event.builder()
                .id(eventId)
                .nombre("Nombre Antiguo")
                .descripcion("Descripción Antigua")
                .fecha(LocalDateTime.of(2026, 10, 15, 20, 0))
                .venue(venue)
                .active(true)
                .build();

        EventCreateDTO requestDTO = new EventCreateDTO(
                "Nombre Actualizado",
                LocalDateTime.of(2026, 11, 20, 18, 0),
                "Descripción Actualizada",
                venueId,
                Set.of()
        );

        Event updatedEvent = Event.builder()
                .id(eventId)
                .nombre("Nombre Actualizado")
                .descripcion("Descripción Actualizada")
                .fecha(LocalDateTime.of(2026, 11, 20, 18, 0))
                .venue(venue)
                .active(true)
                .build();

        // DTO esperado que debe retornar el mapper
        EventResponseDTO expectedResponseDTO = new EventResponseDTO(
                eventId,
                "Nombre Actualizado",
                LocalDateTime.of(2026, 11, 20, 18, 0),
                "Descripción Actualizada",
                "Teatro Municipal",
                Set.of()
        );

        // Stubbing de Mocks
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);

        // <-- ESTA LÍNEA FALTABA: Configurar el mock del mapper
        when(eventMapper.toResponse(any(Event.class))).thenReturn(expectedResponseDTO);

        // 2. ACT
        EventResponseDTO response = eventService.update(eventId, requestDTO);

        // 3. ASSERT
        assertNotNull(response);
        assertEquals("Nombre Actualizado", response.nombre());

        // Verificaciones adicionales de interacción
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(any(Event.class));
        verify(eventMapper).toResponse(any(Event.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingEvent() {

        // Datos que intentaríamos utilizar para actualizar.
        EventCreateDTO updatedEventDTO = new EventCreateDTO(
                "Conferencia Spring Boot",
                LocalDateTime.of(2026, 9, 10, 15, 0),
                "Conferencia sobre Spring Boot",
                1L,
                Set.of()
        );

        // Simulamos que el Repository NO encuentra el evento.
        when(eventRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Comprobamos que el Service lance la excepción.
        assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.update(999L, updatedEventDTO)
        );

        // Verificamos que se haya buscado el ID correcto.
        verify(eventRepository).findById(999L);

        // Como el evento no existe, nunca deberia ejecutarse save().
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldDeleteEventWhenIdExists() {
        // 1. Arrange
        Long eventId = 1L;
        Event mockEvent = new Event();
        mockEvent.setId(eventId);

        // Mockear la búsqueda para que el servicio encuentre el evento antes de borrarlo
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(mockEvent));

        // 2. Act
        eventService.delete(eventId);

        // 3. Assert
        verify(eventRepository).delete(mockEvent); // o deleteById(eventId), según tu implementación
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