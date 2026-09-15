package com.eventify.semana_2.service;

import com.eventify.semana_2.exception.ResourceNotFoundException;
import com.eventify.semana_2.model.Event;
import com.eventify.semana_2.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public Event create(Event event) {
        if (event.getNombre() == null || event.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        return eventRepository.save(event);
    }

    public Page<Event> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "El evento con id: '" + id + "' no fue encontrado."
                        )
                );
    }

    public List<Event> findByNombreContaining(String nombre) {
        return eventRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Event update(Long id, Event event) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El evento con id: '" + id + "' no fue encontrado."));

        if (event.getNombre() == null || event.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        existingEvent.setNombre(event.getNombre());
        existingEvent.setDescripcion(event.getDescripcion());
        existingEvent.setFecha(event.getFecha());

        return eventRepository.save(existingEvent);
    }

    public void delete(Long id) {
        eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El evento con id: '" + id + "' no fue encontrado."));

        eventRepository.deleteById(id);
    }
}
