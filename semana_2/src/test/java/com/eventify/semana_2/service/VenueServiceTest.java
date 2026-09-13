package com.eventify.semana_2.service;

import com.eventify.semana_2.model.Venue;
import com.eventify.semana_2.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/*
 * @ExtendWith(MockitoExtension.class)
 * Esto le indica a Junit 5 que de be usar la extension de mockito
 * Con esto Mockito incializa automaticamente los @Mocks
 */
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

    // @BeforeEach se ejecuta antes de cada test
    @BeforeEach
    void setUp() {
        // Se crea manualmente el service y se inyecta el repositorio falso
        venueService = new VenueService(venueRepository);
    }

    @Test
    void shouldCreateVenueWhenDataIsValid() {

        Venue venue = Venue.builder()
                .id(1L)
                .nombre("Plaza las Americas")
                .direccion("Mz B Lote 5, Barrio Mango Azul")
                .capacidad(150)
                .build();

        // Configurar el comportamiento del mock
        // EL mock devolverá el mismo Venue cuando el service llame a venueRepository.save(venue)
        when(venueRepository.save(venue)).thenReturn(venue);

        Venue result = venueService.create(venue);

        // Assert, comprueaba que el service devuelva el venue esperado
        assertEquals(venue, result);

        // Verify, comprueba que el service realmente llamó al metodo save() del repository
        verify(venueRepository).save(venue);
    }

    @Test
    void shouldRejectVenueWhenNameIsEmpty() {
        Venue venue = new Venue(
                1L,
                "",
                "Mz B Lote 5, Barrio Mango Azul",
                150
        );

        // Comprobando si venueService.create(event) lanza una IllegalArgumentException
        // assertThrows tambien permite obtener la exception lanzada
        assertThrows(
                IllegalArgumentException.class, () -> venueService.create(venue)
        );

        // Comprobamos que no se guardó
        verify(venueRepository, never()).save(venue);
    }

    @Test
    void shouldRejectVenueWhenCapacityIsInvalid() {
        Venue venue = new Venue(
                1L,
                "Plaza las Americas",
                "Mz B Lote 5, Barrio Mango Azul",
                -1
        );

        // Comprobando si venueService.create(event) lanza una IllegalArgumentException
        assertThrows(
                IllegalArgumentException.class, () -> venueService.create(venue)
        );

        // Comprobamos que no se guardó
        verify(venueRepository, never()).save(venue);
    }

    @Test
    void shouldReturnAllVenues() {
        List<Venue> venues = List.of(
                new Venue(
                        1L,
                        "Plaza las Americas",
                        "Mz B Lote 5, Barrio Mango Azul",
                        150
                ), new Venue(
                        2L,
                        "Plaza Europea",
                        "Mz B Lote 5, Barrio Mango Azul",
                        150
                ));

        // Configurar el comportamiento del mock
        // EL mock devolverá el listados de venues
        when(venueRepository.findAll()).thenReturn(venues);

        List<Venue> result = venueService.findAll();

        // Assert, comprueaba que el service devuelva el listado de venues esperado
        assertEquals(venues, result);

        // Verify, comprueba que el servicio llamó al metodo findAll() del repository
        verify(venueRepository).findAll();
    }
}