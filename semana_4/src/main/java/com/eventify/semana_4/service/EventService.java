package com.eventify.semana_4.service;

import com.eventify.semana_4.exception.ResourceNotFoundException;
import com.eventify.semana_4.model.Category;
import com.eventify.semana_4.model.Event;
import com.eventify.semana_4.model.Venue;
import com.eventify.semana_4.repository.CategoryRepository;
import com.eventify.semana_4.repository.EventRepository;
import com.eventify.semana_4.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;

    public Event create(Event event) {
        validateEvent(event);

        // Busca el lugar real en la base de datos y lo asocia al evento.
        event.setVenue(resolveVenue(event));

        // Reemplaza las categorías recibidas por entidades existentes y gestionadas por JPA.
        event.setCategories(resolveCategories(event.getCategories()));

        // Garantiza que un evento nuevo quede activo.
        event.setActive(true);

        // Guarda el evento y su relación con el lugar y las categorías.
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Page<Event> findAll(Pageable pageable) {
        // Obtiene los eventos activos, paginados según el filtro global de la entidad.
        return eventRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Event findById(Long id) {
        // @SQLRestriction excluye automáticamente los eventos inactivos.
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El evento con id: '" + id + "' no fue encontrado."
                ));
    }

    @Transactional(readOnly = true)
    public List<Event> findByNombreContaining(String nombre) {
        // Busca por nombre sin distinguir mayúsculas y minúsculas.
        return eventRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Event update(Long id, Event event) {
        // Busca el evento existente; si está inactivo, no aparecerá por @SQLRestriction.
        Event existingEvent = findById(id);

        // Valida los campos obligatorios recibidos.
        validateEvent(event);

        // Actualiza los datos básicos del evento.
        existingEvent.setNombre(event.getNombre());
        existingEvent.setDescripcion(event.getDescripcion());
        existingEvent.setFecha(event.getFecha());

        // Actualiza el lugar y las categorías usando registros existentes.
        existingEvent.setVenue(resolveVenue(event));
        existingEvent.setCategories(resolveCategories(event.getCategories()));

        // Guarda los cambios del evento y sus asociaciones.
        return eventRepository.save(existingEvent);
    }

    public void delete(Long id) {
        // Busca el evento activo; si no existe, lanza el error de recurso no encontrado.
        Event event = findById(id);

        // Borrado lógico: conserva el registro y lo marca como inactivo.
        event.setActive(false);

        // Persiste el cambio de estado, sin eliminar físicamente el registro.
        eventRepository.save(event);
    }

    private void validateEvent(Event event) {
        // Impide guardar un evento sin nombre.
        if (event.getNombre() == null || event.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        // La relación con Venue es obligatoria.
        if (event.getVenue() == null || event.getVenue().getId() == null) {
            throw new IllegalArgumentException("El lugar es obligatorio");
        }

        // La fecha es obligatoria según el modelo de Event.
        if (event.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }

        // La descripción es obligatoria según el modelo de Event.
        if (event.getDescripcion() == null || event.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
    }

    private Venue resolveVenue(Event event) {
        // Obtiene el ID del lugar enviado y devuelve el registro persistido correspondiente.
        Long venueId = event.getVenue().getId();

        return venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El lugar con id: '" + venueId + "' no fue encontrado."
                ));
    }

    private Set<Category> resolveCategories(Set<Category> categories) {
        // Si no se enviaron categorías, devuelve un conjunto vacío.
        if (categories == null || categories.isEmpty()) {
            return new HashSet<>();
        }

        // Por cada categoría recibida, exige un ID y recupera su entidad persistida.
        Set<Category> resolvedCategories = new HashSet<>();

        for (Category category : categories) {
            if (category == null || category.getId() == null) {
                throw new IllegalArgumentException("Cada categoría seleccionada debe tener un ID");
            }

            Category existingCategory = categoryRepository.findById(category.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "La categoría con id: '" + category.getId() + "' no fue encontrada."
                    ));
            resolvedCategories.add(existingCategory);
        }

        return resolvedCategories;
    }
}