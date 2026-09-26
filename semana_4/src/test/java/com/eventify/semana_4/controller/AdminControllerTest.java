package com.eventify.semana_4.controller;

import com.eventify.semana_4.model.Event;
import com.eventify.semana_4.model.Venue;
import com.eventify.semana_4.service.EventService;
import com.eventify.semana_4.service.VenueService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Imports para test (get)
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

// Imports para test (Post)
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

//  realiza pruebas unitarias focalizadas en la capa web, cargando única y exclusivamente los componentes
//  necesarios para probar el controlador especificado
@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // @MockitoBean reemplaza el EventService real dentro del contexto de Spring por un mock de Mockito
    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private VenueService venueService;

    @Test
    void shouldReturnEventsPageWithCorrectData() throws Exception {
        // 1. ARRANGEMENT (Preparación)
        Event event = Event.builder()
                .id(1L)
                .nombre("Concierto de Rock")
                .descripcion("Evento musical")
                .fecha(LocalDateTime.of(2026, 10, 15, 20, 0))
                .build();

        Page<Event> page = new PageImpl<>(List.of(event));
        when(eventService.findAll(any(Pageable.class))).thenReturn(page);

        // 2. ACT & ASSERT (Acción y Verificación combinada)
        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk()) // Valida HTTP 200
                .andExpect(view().name("admin/events")) // Valida la vista HTML
                .andExpect(model().attribute("events", page)); // Valida que el Model contiene los datos esperados
    }

    @Test
    void shouldReturnVenuesPageWithCorrectData() throws Exception {
        // 1. ARRANGEMENT (Preparación)
        Venue venue = Venue.builder()
                .id(1L)
                .nombre("Concierto de Rock")
                .direccion("parque los Andes")
                .capacidad(250)
                .build();

        Page<Venue> page = new PageImpl<>(List.of(venue));
        when(venueService.findAll(any(Pageable.class))).thenReturn(page);

        // 2. ACT & ASSERT (Acción y Verificación combinada)
        mockMvc.perform(get("/admin/venues"))
                .andExpect(status().isOk()) // Valida HTTP 200
                .andExpect(view().name("admin/venues")) // Valida la vista HTML
                .andExpect(model().attribute("venues", page)); // Valida que el Model contiene los datos esperados
    }

    /// Test de Formularios
    @Test
    void shouldReturnEventForm() throws Exception {

        // Simula GET /admin/events/new
        mockMvc.perform(get("/admin/events/new"))
                // Verifica HTTP 200
                .andExpect(status().isOk())
                // Verifica que se devuelve la vista correcta
                .andExpect(view().name("admin/event-form"))
                // Verifica que el Model contiene "event"
                .andExpect(model().attributeExists("event"));
    }

    @Test
    void shouldReturnVenueForm() throws Exception {

        // Simula GET /admin/events/new
        mockMvc.perform(get("/admin/venues/new"))
                // Verifica HTTP 200
                .andExpect(status().isOk())
                // Verifica que se devuelve la vista correcta
                .andExpect(view().name("admin/venue-form"))
                // Verifica que el Model contiene "venue"
                .andExpect(model().attributeExists("venue"));
    }

    /// Test de formularios Post
    @Test
    void shouldCreateEventAndRedirect() throws Exception {

        // Evento que simulará ser creado por el servicio.
        Event event = Event.builder()
                .id(1L)
                .nombre("Concierto de Rock")
                .descripcion("Evento musical")
                .fecha(LocalDateTime.of(2026, 10, 15, 20, 0))
                .build();

        // Simula que el servicio guarda el evento correctamente.
        when(eventService.create(any(Event.class))).thenReturn(event);

        // Simula POST /admin/events enviando los datos del formulario.
        mockMvc.perform(post("/admin/events")
                        .param("nombre", "Concierto de Rock")
                        .param("descripcion", "Evento musical")
                        .param("fecha", "2026-10-15T20:00"))

                // Verifica que el controlador responde con una redirección.
                .andExpect(status().is3xxRedirection())

                // Verifica que redirige al listado de eventos.
                .andExpect(redirectedUrl("/admin/events"));

        // Verifica que el controlador realmente llamó al servicio.
        verify(eventService).create(any(Event.class));
    }

    @Test
    void shouldCreateVenueAndRedirect() throws Exception {

        // Evento que simulará ser creado por el servicio.
        Venue venue = Venue.builder()
                .id(1L)
                .nombre("Concierto de Rock")
                .direccion("Parque los Andes")
                .capacidad(150)
                .build();

        // Simula que el servicio guarda el evento correctamente.
        when(venueService.create(any(Venue.class))).thenReturn(venue);

        // Simula POST /admin/events enviando los datos del formulario.
        mockMvc.perform(post("/admin/venues")
                        .param("nombre", "Concierto de Rock")
                        .param("direccion", "Parque los Andes")
                        .param("capacidad", String.valueOf(150)))

                // Verifica que el controlador responde con una redirección.
                .andExpect(status().is3xxRedirection())

                // Verifica que redirige al listado de eventos.
                .andExpect(redirectedUrl("/admin/venues"));

        // Verifica que el controlador realmente llamó al servicio.
        verify(venueService).create(any(Venue.class));
    }
}
