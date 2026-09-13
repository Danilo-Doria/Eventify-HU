package com.eventify.semana_2.repository;

import com.eventify.semana_2.model.Event;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EventRepository {
    private final List<Event> events = new ArrayList<>();

    public Event save(Event event) {
        events.add(event);
        return event;
    }

    public List<Event> findAll() {
        return events;
    }
}