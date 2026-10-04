package com.eventify.semana_5.dto.mapper;

import com.eventify.semana_5.dto.VenueCreateDTO;
import com.eventify.semana_5.dto.VenueResponseDTO;
import com.eventify.semana_5.model.Venue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Indica a MapStruct que genere la implementación y la registre como bean de Spring.
@Mapper(componentModel = "spring")
public interface VenueMapper {
    // Convierte los datos recibidos en el DTO a una entidad Venue.
    @Mapping(target = "id", ignore = true)
    Venue toEntity(VenueCreateDTO dto);

    // Convierte una entidad Venue en el DTO que se devuelve al cliente.
    VenueResponseDTO toResponse(Venue venue);
}
