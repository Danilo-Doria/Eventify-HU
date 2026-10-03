package com.eventify.semana_5.controller;

import com.eventify.semana_5.dto.EventSummaryDTO;
import com.eventify.semana_5.model.Category;
import com.eventify.semana_5.model.Event;
import com.eventify.semana_5.model.Venue;
import com.eventify.semana_5.service.CategoryService;
import com.eventify.semana_5.service.EventService;
import com.eventify.semana_5.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller // Esta clase manejará solicitudes HTTP relacionadas con páginas web
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EventService eventService;
    private final VenueService venueService;
    private final CategoryService categoryService;

    // Model es el objeto que Spring nos proporciona para transportar información desde el Controller hacia la vista
    @GetMapping("/events")

    public String events(
            // Mantiene la paginación y el orden cronológico descendente por defecto.
            @PageableDefault(
                    size = 10,
                    sort = "fecha",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable,

            // Filtros opcionales recibidos desde el formulario de búsqueda.
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Integer capacidad,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin,

            Model model) {

        // Obtiene los eventos resumidos aplicando los filtros indicados.
        Slice<EventSummaryDTO> events = eventService.findEventSummariesWithFilters(
                ciudad,
                categoria,
                capacidad,
                fechaInicio,
                fechaFin,
                pageable
        );

        // Envía el resultado y los filtros a la vista para conservar sus valores.
        model.addAttribute("events", events);
        model.addAttribute("ciudad", ciudad);
        model.addAttribute("categoria", categoria);
        model.addAttribute("capacidad", capacidad);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);

        // Renderiza templates/admin/events.html.
        return "admin/events";
    }

    @GetMapping("/events/new")
    public String newEvent(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("venues", venueService.findAll());
        model.addAttribute("categories", categoryService.findAll());

        return "admin/event-form";
    }

    @PostMapping("/events")
    // @ModelAttribute Vincula los datos enviados desde el formulario HTML (th:object="${event}") a este objeto Java
    public String createEvent(
            @ModelAttribute Event event,
            @RequestParam(required = false) List<Long> categoryIds) {

        // Crea un conjunto vacío de categorías para el evento.
        Set<Category> categories = new HashSet<>();

        // Si el formulario recibió categorías, crea referencias usando sus IDs.
        if (categoryIds != null) {
            for (Long categoryId : categoryIds) {

                // Crea una Category únicamente con su ID para que el Service la resuelva en la base de datos.
                Category category = Category.builder()
                        .id(categoryId)
                        .build();

                categories.add(category);
            }
        }

        // Asocia las categorías recibidas al evento antes de enviarlo al Service.
        event.setCategories(categories);

        // Envía el evento al servicio para validarlo y guardarlo junto con sus relaciones.
        eventService.create(event);

        // Redirige al listado después de guardar correctamente.
        return "redirect:/admin/events";
    }

    @GetMapping("/venues")
    public String venues(
            @PageableDefault(
                    size = 10,
                    sort = "nombre",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable,
            Model model) {

        // Obtiene los venues paginados desde la base de datos.
        Page<Venue> venues = venueService.findAll(pageable);

        // Envía los venues al modelo para que Thymeleaf pueda mostrarlos.
        model.addAttribute("venues", venues);

        // Renderiza la vista templates/admin/venues.html.
        return "admin/venues";
    }

    @GetMapping("/venues/new")
    public String newVenue(Model model) {

        // Crea un objeto Venue vacío para enlazarlo con el formulario.
        model.addAttribute("venue", new Venue());

        // Renderiza templates/admin/venue-form.html.
        return "admin/venue-form";
    }

    @PostMapping("/venues")
    // @ModelAttribute Vincula los datos enviados desde el formulario HTML (th:object="${venue}") a este objeto Java
    public String createVenue(@ModelAttribute Venue venue) {

        // Envía el evento recibido al servicio para validarlo y guardarlo.
        venueService.create(venue);

        // Redirige al listado después de guardar correctamente.
        return "redirect:/admin/venues";
    }
}
