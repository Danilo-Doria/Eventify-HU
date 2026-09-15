package com.eventify.semana_2.repository;

import com.eventify.semana_2.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByNombreContainingIgnoreCase(String nombre);

}