package com.eventify.semana_2.service;

import com.eventify.semana_2.exception.ResourceNotFoundException;
import com.eventify.semana_2.model.Event;
import com.eventify.semana_2.repository.EventRepository;
import lombok.RequiredArgsConstructor;
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

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event findBtId(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "El evento con id: '" + id + "' no fue encontrado."
                        )
                );
    }
}
