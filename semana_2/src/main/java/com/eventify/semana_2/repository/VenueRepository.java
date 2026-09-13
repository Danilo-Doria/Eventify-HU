package com.eventify.semana_2.repository;

import com.eventify.semana_2.model.Venue;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class VenueRepository {
    private final List<Venue> venues = new ArrayList<>();

    public Venue save(Venue venue) {
        venues.add(venue);
        return venue;
    }

    public List<Venue> findAll() {
        return venues;
    }
}