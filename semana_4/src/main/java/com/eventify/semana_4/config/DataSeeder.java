package com.eventify.semana_4.config;

import com.eventify.semana_4.model.Event;
import com.eventify.semana_4.model.Venue;
import com.eventify.semana_4.repository.EventRepository;
import com.eventify.semana_4.repository.VenueRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {
    /**
     * CommandLineRunner es una interfaz de Spring Boot utilizada para ejecutar
     * código en la fase final del arranque de la aplicación.
     * <p>
     * Se utiliza para tareas de inicialización (como poblar la base de datos)
     * asegurando que todos los componentes, repositorios y configuraciones
     * ya estén completamente instanciados y listos.
     */
    @Bean
    public CommandLineRunner initData(EventRepository eventRepository, VenueRepository venueRepository) {
        return args -> {
            if (venueRepository.count() == 0) {
                Venue venue = Venue.builder().nombre("Centro de Convenciones").direccion("Calle 50 # 10-20").capacidad(500).ciudad("Bogotá").build();
                venueRepository.save(venue);
            }
            if (eventRepository.count() == 0) {
                // Obtiene un Venue existente para asociarlo obligatoriamente al evento.
                Venue venue = venueRepository.findAll().get(0);
                Event event = Event.builder().nombre("Evento de prueba").fecha(LocalDateTime.of(2026, 10, 15, 19, 30)).descripcion("Evento inicial").venue(venue).build();
                eventRepository.save(event);
            }
        };
    }
}

/*
 *Otra forma de hacer el seeder aunque un poco ineficiente ya que se Mantienes instancias individuales
 * de datos de prueba en el contenedor IoC durante toda la vida de la app
@Configuration
public class DataSeeder {

    @Bean
    public Event initialEvent(EventRepository eventRepository) {

        Event event = new Event(
                1L,
                "Evento de prueba",
                LocalDateTime.of(2026, 10, 15, 19, 30),
                "Evento inicial de Eventify"
        );

        return eventRepository.save(event);
    }

    @Bean
    public Venue initialVenue(VenueRepository venueRepository) {

        Venue venue = new Venue(
                1L,
                "Centro de Convenciones",
                "Calle 50 # 10-20",
                500
        );

        return venueRepository.save(venue);
    }
}
 */