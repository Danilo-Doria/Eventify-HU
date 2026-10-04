package com.eventify.semana_5.service;

import com.eventify.semana_5.dto.EventCreateDTO;
import com.eventify.semana_5.dto.EventResponseDTO;
import com.eventify.semana_5.dto.EventSummaryDTO;
import com.eventify.semana_5.dto.mapper.EventMapper;
import com.eventify.semana_5.exception.ResourceNotFoundException;
import com.eventify.semana_5.model.Category;
import com.eventify.semana_5.model.Event;
import com.eventify.semana_5.model.Venue;
import com.eventify.semana_5.repository.CategoryRepository;
import com.eventify.semana_5.repository.EventRepository;
import com.eventify.semana_5.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventResponseDTO create(EventCreateDTO dto) {
        if (dto.nombre() == null || dto.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del evento no puede estar vacío");
        }
        Venue venue = venueRepository.findById(dto.venueId())
                .orElseThrow(() -> new ResourceNotFoundException("La sede con ID " + dto.venueId() + " no existe"));

        Event event = eventMapper.toEntity(dto);
        event.setVenue(venue);
        event.setActive(true);

        if (dto.categoryIds() != null && !dto.categoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(dto.categoryIds());
            event.setCategories(new HashSet<>(categories));
        }

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public EventResponseDTO findByIdDTO(Long id) {
        Event event = findById(id);
        return eventMapper.toResponse(event);
    }

    @Transactional(readOnly = true)
    public Slice<EventSummaryDTO> findAllSummary(Pageable pageable) {
        return eventRepository.findEventSummaries(pageable);
    }

    @Transactional(readOnly = true)
    public Slice<EventSummaryDTO> findEventSummariesWithFilters(
            String ciudad,
            String categoria,
            Integer capacidad,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable) {

        return eventRepository.findEventSummariesWithFilters(
                ciudad,
                categoria,
                capacidad,
                fechaInicio,
                fechaFin,
                pageable
        );
    }

    @Transactional
    public EventResponseDTO update(Long id, EventCreateDTO dto) {
        Event existingEvent = findById(id);

        existingEvent.setNombre(dto.nombre());
        existingEvent.setFecha(dto.fecha());
        existingEvent.setDescripcion(dto.descripcion());

        if (!existingEvent.getVenue().getId().equals(dto.venueId())) {
            Venue newVenue = venueRepository.findById(dto.venueId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "El lugar con id: '" + dto.venueId() + "' no fue encontrado."
                    ));
            existingEvent.setVenue(newVenue);
        }

        if (dto.categoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(dto.categoryIds()));
            if (!dto.categoryIds().isEmpty() && categories.size() != dto.categoryIds().size()) {
                throw new ResourceNotFoundException("Una o más categorías especificadas no existen.");
            }
            existingEvent.setCategories(categories);
        }

        Event updatedEvent = eventRepository.save(existingEvent);
        return eventMapper.toResponse(updatedEvent);
    }

    @Transactional(readOnly = true)
    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El evento con id: '" + id + "' no fue encontrado."
                ));
    }

    @Transactional
    public void delete(Long id) {
        Event event = findById(id);
        eventRepository.delete(event);
    }
}