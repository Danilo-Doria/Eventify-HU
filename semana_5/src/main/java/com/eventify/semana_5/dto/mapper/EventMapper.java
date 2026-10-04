package com.eventify.semana_5.dto.mapper;

import com.eventify.semana_5.dto.EventCreateDTO;
import com.eventify.semana_5.dto.EventResponseDTO;
import com.eventify.semana_5.model.Category;
import com.eventify.semana_5.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "active", ignore = true)
    Event toEntity(EventCreateDTO dto);

    @Mapping(target = "venueName", source = "venue.nombre")
    @Mapping(target = "categoryNames", source = "categories", qualifiedByName = "mapCategoriesToNames")
    EventResponseDTO toResponse(Event event);

    @Mapping(target = "venueId", source = "venue.id")
    @Mapping(target = "categoryIds", source = "categories", qualifiedByName = "mapCategoriesToIds")
    EventCreateDTO toCreateDTO(Event event);

    @Named("mapCategoriesToNames")
    default Set<String> mapCategoriesToNames(Set<Category> categories) {
        if (categories == null) return Set.of();
        return categories.stream().map(Category::getNombre).collect(Collectors.toSet());
    }

    @Named("mapCategoriesToIds")
    default Set<Long> mapCategoriesToIds(Set<Category> categories) {
        if (categories == null) return Set.of();
        return categories.stream().map(Category::getId).collect(Collectors.toSet());
    }
}