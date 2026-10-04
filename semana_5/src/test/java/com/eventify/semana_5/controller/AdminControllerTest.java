package com.eventify.semana_5.controller;

import com.eventify.semana_5.dto.EventSummaryDTO;
import com.eventify.semana_5.dto.VenueCreateDTO;
import com.eventify.semana_5.dto.VenueResponseDTO;
import com.eventify.semana_5.service.CategoryService;
import com.eventify.semana_5.service.EventService;
import com.eventify.semana_5.service.VenueService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Imports para test (get)
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

// Imports para test (Post)
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

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void shouldReturnEventsPageWithCorrectData() throws Exception {
        // Datos que simularán el resumen de un evento.
        EventSummaryDTO event = new EventSummaryDTO(
                "Concierto de Rock",
                LocalDateTime.of(2026, 10, 15, 20, 0),
                "Centro de Convenciones", "Bogotá");
        // Slice que simulará la respuesta del servicio.
        Slice<EventSummaryDTO> slice = new SliceImpl<>(List.of(event));
        // Simula la consulta optimizada del catálogo administrativo.
        when(eventService.findEventSummariesWithFilters(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(Pageable.class)
        )).thenReturn(slice);

        // Simula GET /admin/events.
        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events"))
                .andExpect(model().attribute("events", slice));
    }

    @Test
    void shouldReturnVenuesPageWithCorrectData() throws Exception {
        // 1. ARRANGEMENT (Preparación)
        VenueResponseDTO venueResponse = new VenueResponseDTO(
                1L,
                "Concierto de Rock",
                "parque los Andes",
                250,
                "Barranquilla"
        );

        Page<VenueResponseDTO> page = new PageImpl<>(List.of(venueResponse));
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

        // Simula GET /admin/venues/new
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
        mockMvc.perform(post("/admin/events")
                        .param("nombre", "Concierto de Rock")
                        .param("descripcion", "Evento musical")
                        .param("fecha", "2026-10-15T20:00")
                        .param("venueId", "1")
                        .param("categoryIds", "1") // <-- AGREGA ESTE PARÁMETRO (puedes pasar "1", "2", etc.)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"));
    }

    @Test
    void shouldCreateVenueAndRedirect() throws Exception {

        // DTO de respuesta que simulará ser retornado por el servicio.
        VenueResponseDTO venueResponse = new VenueResponseDTO(
                1L,
                "Concierto de Rock",
                "Parque los Andes",
                150,
                "Barranquilla"
        );

        // Simula que el servicio recibe un VenueCreateDTO y guarda la sede correctamente.
        when(venueService.create(any(VenueCreateDTO.class))).thenReturn(venueResponse);

        // Simula POST /admin/venues enviando los datos del formulario.
        mockMvc.perform(post("/admin/venues")
                        .param("nombre", "Concierto de Rock")
                        .param("direccion", "Parque los Andes")
                        .param("capacidad", "150")
                        .param("ciudad", "Barranquilla"))

                // Verifica que el controlador responde con una redirección.
                .andExpect(status().is3xxRedirection())

                // Verifica que redirige al listado de sedes.
                .andExpect(redirectedUrl("/admin/venues"));

        // Verifica que el controlador realmente llamó al servicio pasando un VenueCreateDTO.
        verify(venueService).create(any(VenueCreateDTO.class));
    }
}