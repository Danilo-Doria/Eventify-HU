package com.eventify.semana_2.service;

import com.eventify.semana_2.exception.ResourceNotFoundException;
import com.eventify.semana_2.model.Venue;
import com.eventify.semana_2.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

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
        // assertThrows tambin permite obtener la exception lanzada
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
    void shouldReturnAllVenuesWithPagination() {
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

        // Creamos el Pageable que queremos simular:
        // página 0 y 2 elementos por página.
        Pageable pageable = PageRequest.of(0, 2);

        // Convertimos nuestra lista en un objeto Page.
        Page<Venue> venuePage = new PageImpl<>(venues);

        // Configuramos el comportamiento del mock.
        // Cuando el Repository reciba ese Pageable,
        // devolverá nuestra página simulada.
        when(venueRepository.findAll(pageable)).thenReturn(venuePage);

        // Ejecutamos el metodo del Service.
        Page<Venue> result = venueRepository.findAll(pageable);

        // Comprobamos que el Service devuelve la página esperada.
        assertEquals(venuePage, result);

        // Comprobamos que el Repository fue llamado correctamente.
        verify(venueRepository).findAll(pageable);
    }

    @Test
    void shouldReturnVenueWhenIdExists() {
        Venue venue = new Venue(
                1L,
                "Plaza las Americas",
                "Mz B Lote 5, Barrio Mango Azul",
                150
        );

        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));

        // Act
        Venue result = venueService.findById(1L);

        // Assert
        assertEquals(venue, result);
        verify(venueRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenVenueIdDoesNotExist() {
        when(venueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> venueService.findById(999L)
        );
        verify(venueRepository).findById(999L);
    }

    @Test
    void shouldUpdateVenueWhenIdExists() {
        // Datos existentes en la BD simulada
        Venue existingVenue = new Venue(
                1L,
                "Plaza las Americas",
                "Mz B Lote 5, Barrio Mango Azul",
                150
        );

        // Nuevos datos para actualizar
        Venue updatedVenue = new Venue(
                null,
                "Plaza las Americas Renovada",
                "Nueva Direccion 123",
                200
        );

        when(venueRepository.findById(1L)).thenReturn(Optional.of(existingVenue));
        when(venueRepository.save(existingVenue)).thenReturn(existingVenue);

        // Act
        Venue result = venueService.update(1L, updatedVenue);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Plaza las Americas Renovada", result.getNombre());
        assertEquals("Nueva Direccion 123", result.getDireccion());
        assertEquals(200, result.getCapacidad());

        verify(venueRepository).findById(1L);
        verify(venueRepository).save(existingVenue);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingVenue() {
        Venue updatedVenue = new Venue(
                null,
                "Plaza las Americas Renovada",
                "Nueva Direccion 123",
                200
        );

        when(venueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> venueService.update(999L, updatedVenue)
        );
        verify(venueRepository).findById(999L);
        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    void shouldDeleteVenueWhenIdExists() {
        Venue venue = new Venue(
                1L,
                "Plaza las Americas",
                "Mz B Lote 5, Barrio Mango Azul",
                150
        );

        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));

        venueService.delete(1L);

        verify(venueRepository).findById(1L);
        verify(venueRepository).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingVenue() {
        when(venueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> venueService.delete(999L)
        );
        verify(venueRepository).findById(999L);
        verify(venueRepository, never()).deleteById(999L);
    }
}