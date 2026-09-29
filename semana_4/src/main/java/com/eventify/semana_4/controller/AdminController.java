package com.eventify.semana_4.controller;

import com.eventify.semana_4.dto.EventSummaryDTO;
import com.eventify.semana_4.model.Category;
import com.eventify.semana_4.model.Event;
import com.eventify.semana_4.model.Venue;
import com.eventify.semana_4.service.CategoryService;
import com.eventify.semana_4.service.EventService;
import com.eventify.semana_4.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
            @ParameterObject
            @PageableDefault(
                    size = 10,
                    sort = "nombre",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable,
            Model model) {

        // Obtiene los eventos mediante una proyección optimizada y paginada con Slice.
        Slice<EventSummaryDTO> events = eventService.findEventSummaries(pageable);

        // Envía el resumen paginado de eventos a la vista.
        model.addAttribute("events", events);

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
