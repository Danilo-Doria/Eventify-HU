package com.eventify.semana_1.service;

import com.eventify.semana_1.model.Venue;
import com.eventify.semana_1.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

// Forma de crear test unitarios un poco mas "moderna"
@ExtendWith(MockitoExtension.class)
public class VenueServiceTest {

    // Crea un repositorio falso para pruebas, ya que se quiere testear el servicio
    @Mock
    private VenueRepository venueRepository;

    private VenueService venueService;

    /*
    @InjectMocks
    private VenueService venueService;

    Con esta anotacion de @InjectMocks mockito contruye el objeto a travez de reflexion
    (Capacidad de un programa de manupular y examinar su propio codigo en tiempo de ejecucion)
     */

    @BeforeEach
    void setUp() {
        // Se crea manualmente el service y se inyecta el repositorio falso
        venueService = new VenueService(venueRepository);
    }

    @Test
    void shouldCreateVenueWhenDataIsValid() {
        Venue venue = new Venue(
                1L,
                "Plaza las Americas",
                "Mz B Lote 5, Barrio Mango Azul",
                150
        );

        // Configurar el comportamiento del mock
        // EL mock devolverá el mismo Venue
        when(venueRepository.save(venue)).thenReturn(venue);

        Venue result = venueService.create(venue);

        // Assert, compruevea
    }
}
