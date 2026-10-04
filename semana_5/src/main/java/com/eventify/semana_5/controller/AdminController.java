package com.eventify.semana_5.controller;

import com.eventify.semana_5.dto.EventCreateDTO;
import com.eventify.semana_5.dto.EventSummaryDTO;
import com.eventify.semana_5.dto.VenueCreateDTO;
import com.eventify.semana_5.dto.VenueResponseDTO;
import com.eventify.semana_5.service.CategoryService;
import com.eventify.semana_5.service.EventService;
import com.eventify.semana_5.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EventService eventService;
    private final VenueService venueService;
    private final CategoryService categoryService;

    @GetMapping("/events")
    public String events(
            @PageableDefault(
                    size = 10,
                    sort = "fecha",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable,

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

        Slice<EventSummaryDTO> events = eventService.findEventSummariesWithFilters(
                ciudad,
                categoria,
                capacidad,
                fechaInicio,
                fechaFin,
                pageable
        );

        model.addAttribute("events", events);
        model.addAttribute("ciudad", ciudad);
        model.addAttribute("categoria", categoria);
        model.addAttribute("capacidad", capacidad);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);

        return "admin/events";
    }

    @GetMapping("/events/new")
    public String newEvent(Model model) {
        model.addAttribute("event", new EventCreateDTO(null, null, null, null, null));
        model.addAttribute("venues", venueService.findAll());
        model.addAttribute("categories", categoryService.findAll());

        return "admin/event-form";
    }

    @PostMapping("/events")
    public String createEvent(
            @Valid @ModelAttribute("event") EventCreateDTO dto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("venues", venueService.findAll());
            model.addAttribute("categories", categoryService.findAll());
            return "admin/event-form";
        }

        eventService.create(dto);
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

        Page<VenueResponseDTO> venues = venueService.findAll(pageable);
        model.addAttribute("venues", venues);

        return "admin/venues";
    }

    @GetMapping("/venues/new")
    public String newVenue(Model model) {
        model.addAttribute("venue", new VenueCreateDTO(null, null, null, null));
        return "admin/venue-form";
    }

    @PostMapping("/venues")
    public String createVenue(
            @Valid @ModelAttribute("venue") VenueCreateDTO dto,
            BindingResult result) {

        if (result.hasErrors()) {
            return "admin/venue-form";
        }

        venueService.create(dto);
        return "redirect:/admin/venues";
    }
}