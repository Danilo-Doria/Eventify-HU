package com.eventify.semana_5.service;

import com.eventify.semana_5.dto.VenueCreateDTO;
import com.eventify.semana_5.dto.VenueResponseDTO;
import com.eventify.semana_5.dto.mapper.VenueMapper;
import com.eventify.semana_5.exception.ResourceNotFoundException;
import com.eventify.semana_5.model.Venue;
import com.eventify.semana_5.repository.VenueRepository;
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

    @Mock
    private VenueMapper venueMapper;

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
        venueService = new VenueService(venueRepository, venueMapper);
    }

    @Test
    void shouldCreateVenueWhenDataIsValid() {

        VenueCreateDTO dto = new VenueCreateDTO(
                "Plaza las Americas",
                "Mz B Lote 5, Barrio Mango Azul",
                150,
                "Barranquilla"
        );

        Venue venue = Venue.builder()
                .id(1L)
                .nombre("Plaza las Americas")
                .direccion("Mz B Lote 5, Barrio Mango Azul")
                .capacidad(150)
                .ciudad("Barranquilla")
                .build();

        VenueResponseDTO responseDTO = new VenueResponseDTO(
                1L,
                "Plaza las Americas",
                "Mz B Lote 5, Barrio Mango Azul",
                150,
                "Barranquilla"
        );

        // Configurar el comportamiento del mock
        // EL mock devolverá el mismo Venue cuando el service llame a venueRepository.save(venue)
        when(venueMapper.toEntity(dto)).thenReturn(venue);
        when(venueRepository.save(venue)).thenReturn(venue);
        when(venueMapper.toResponse(venue)).thenReturn(responseDTO);

        VenueResponseDTO result = venueService.create(dto);

        // Assert, comprueaba que el service devuelva el venue esperado
        assertEquals(responseDTO, result);

        // Verify, comprueba que el service realmente llamó al metodo save() del repository
        verify(venueRepository).save(venue);
    }

    @Test
    void shouldRejectVenueWhenNameIsEmpty() {
        VenueCreateDTO dto = new VenueCreateDTO(
                "",
                "Mz B Lote 5, Barrio Mango Azul",
                150,
                "Barranquilla"
        );

        // Comprobando si venueService.create(event) lanza una IllegalArgumentException
        // assertThrows tambin permite obtener la exception lanzada
        assertThrows(
                IllegalArgumentException.class, () -> venueService.create(dto)
        );

        // Comprobamos que no se guardó
        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    void shouldRejectVenueWhenCapacityIsInvalid() {
        VenueCreateDTO dto = new VenueCreateDTO(
                "Plaza las Americas",
                "Mz B Lote 5, Barrio Mango Azul",
                -1,
                "Barranquilla"
        );

        // Comprobando si venueService.create(event) lanza una IllegalArgumentException
        assertThrows(
                IllegalArgumentException.class, () -> venueService.create(dto)
        );

        // Comprobamos que no se guardó
        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    void shouldReturnAllVenuesWithPagination() {
        List<Venue> venues = List.of(
                Venue.builder()
                        .id(1L)
                        .nombre("Plaza las Americas")
                        .direccion("Mz B Lote 5, Barrio Mango Azul")
                        .capacidad(200)
                        .ciudad("Barranquilla")
                        .build(),
                Venue.builder()
                        .id(2L)
                        .nombre("Plaza las Europea")
                        .direccion("Mz A Lote 22")
                        .capacidad(130)
                        .ciudad("Medellin")
                        .build());

        VenueResponseDTO response1 = new VenueResponseDTO(1L, "Plaza las Americas", "Mz B Lote 5, Barrio Mango Azul", 200, "Barranquilla");
        VenueResponseDTO response2 = new VenueResponseDTO(2L, "Plaza las Europea", "Mz A Lote 22", 130, "Medellin");

        // Creamos el Pageable que queremos simular:
        // página 0 y 2 elementos por página.
        Pageable pageable = PageRequest.of(0, 2);

        // Convertimos nuestra lista en un objeto Page.
        Page<Venue> venuePage = new PageImpl<>(venues);
        Page<VenueResponseDTO> expectedPage = new PageImpl<>(List.of(response1, response2));

        // Configuramos el comportamiento del mock.
        // Cuando el Repository reciba ese Pageable,
        // devolverá nuestra página simulada.
        when(venueRepository.findAll(pageable)).thenReturn(venuePage);
        when(venueMapper.toResponse(venues.get(0))).thenReturn(response1);
        when(venueMapper.toResponse(venues.get(1))).thenReturn(response2);

        // Ejecutamos el metodo del Service.
        Page<VenueResponseDTO> result = venueService.findAll(pageable);

        // Comprobamos que el Service devuelve la página esperada.
        assertEquals(expectedPage, result);

        // Comprobamos que el Repository fue llamado correctamente.
        verify(venueRepository).findAll(pageable);
    }

    @Test
    void shouldReturnVenueWhenIdExists() {
        Venue venue = Venue.builder()
                .id(1L)
                .nombre("Plaza las Americas")
                .direccion("Mz B Lote 5, Barrio Mango Azul")
                .capacidad(122)
                .ciudad("Barranquilla")
                .build();

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
        Venue existingVenue = Venue.builder()
                .id(1L)
                .nombre("Plaza las Americas")
                .direccion("Mz B Lote 5, Barrio Mango Azul")
                .capacidad(122)
                .ciudad("Barranquilla")
                .build();

        // Nuevos datos para actualizar
        VenueCreateDTO updatedVenueDTO = new VenueCreateDTO(
                "Plaza las Americas Renovada",
                "Nueva Direccion 123",
                200,
                "Bogota"
        );

        VenueResponseDTO responseDTO = new VenueResponseDTO(
                1L,
                "Plaza las Americas Renovada",
                "Nueva Direccion 123",
                200,
                "Bogota"
        );

        when(venueRepository.findById(1L)).thenReturn(Optional.of(existingVenue));
        when(venueRepository.save(existingVenue)).thenReturn(existingVenue);
        when(venueMapper.toResponse(existingVenue)).thenReturn(responseDTO);

        // Act
        VenueResponseDTO result = venueService.update(1L, updatedVenueDTO);

        // Assert
        assertEquals(1L, result.id());
        assertEquals("Plaza las Americas Renovada", result.nombre());
        assertEquals("Nueva Direccion 123", result.direccion());
        assertEquals(200, result.capacidad());
        assertEquals("Bogota", result.ciudad());

        verify(venueRepository).findById(1L);
        verify(venueRepository).save(existingVenue);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingVenue() {
        VenueCreateDTO updatedVenueDTO = new VenueCreateDTO(
                "Plaza las Americas",
                "Mz B Lote 5, Barrio Mango Azul",
                122,
                "Barranquilla"
        );

        when(venueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> venueService.update(999L, updatedVenueDTO)
        );
        verify(venueRepository).findById(999L);
        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    void shouldDeleteVenueWhenIdExists() {
        Venue venue = Venue.builder()
                .id(1L)
                .nombre("Plaza las Americas")
                .direccion("Mz B Lote 5, Barrio Mango Azul")
                .capacidad(122)
                .ciudad("Barranquilla")
                .build();

        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));

        venueService.delete(1L);

        verify(venueRepository).findById(1L);
        verify(venueRepository).delete(venue);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingVenue() {
        when(venueRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> venueService.delete(999L)
        );
        verify(venueRepository).findById(999L);
        verify(venueRepository, never()).delete(any(Venue.class));
    }
}