package ru.practicum.server.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.server.dto.categoryDtos.CategoryDto;
import ru.practicum.server.dto.categoryDtos.NewCategoryDto;
import ru.practicum.server.dto.compilationDtos.CompilationDto;
import ru.practicum.server.dto.compilationDtos.NewCompilationDto;
import ru.practicum.server.dto.eventDtos.*;
import ru.practicum.server.dto.requestDtos.ParticipationRequestDto;
import ru.practicum.server.dto.requestDtos.UpdateEventUserRequestDto;
import ru.practicum.server.models.Category;
import ru.practicum.server.models.Event;
import ru.practicum.server.repository.entities.*;

import java.time.LocalDateTime;

@Mapper(imports = {LocalDateTime.class, LocationDto.class})
public interface EventMapper {
    EventMapper EVENT_MAPPER = Mappers.getMapper(EventMapper.class);

    Category fromCategoryEntity(CategoryEntity categoryEntity);

    CategoryEntity toCategoryEntity(Category category);

    Category fromCategoryDto(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);

    Category fromNewCategoryDto(NewCategoryDto newCategoryDto);

    @Mapping(target = "participantLimit", source = "limit")
    @Mapping(target = "requestModeration", source = "moderation")
    @Mapping(target = "location", expression = "java(new LocationDto(eventEntity.getLocationLat(), eventEntity.getLocationLon()))")
    @Mapping(target = "requestEntities", expression = "java(eventEntity.getRequestEntities() == null ? new ArrayList<>() : eventEntity.getRequestEntities())")
    Event fromEventEntity(EventEntity eventEntity);

    @Mapping(target = "paid", defaultValue = "false", source = "paid")
    @Mapping(target = "participantLimit", defaultValue = "0", source = "participantLimit")
    @Mapping(target = "requestModeration", defaultValue = "true", source = "requestModeration")
    @Mapping(target = "createdOn", expression = "java(LocalDateTime.now())")
    @Mapping(target = "category", ignore = true)
    Event fromNewEventDto(NewEventDto newEventDto);

    @Mapping(target = "limit", source = "participantLimit")
    @Mapping(target = "moderation", source = "requestModeration")
    @Mapping(target = "state", source = "state", defaultValue = "PENDING")
    @Mapping(target = "locationLat", source = "location.lat")
    @Mapping(target = "locationLon", source = "location.lon")
    EventEntity toEventEntity(Event event);

    @Mapping(target = "initiator", source = "owner")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "initiator", source = "owner")
    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "category", ignore = true)
    Event fromUpdateEventRequest(UpdateEventUserRequestDto updateEventUserRequestDto);

    @Mapping(target = "event", expression = "java(entity.getEventId().getId())")
    @Mapping(target = "requester", expression = "java(entity.getUserId().getId())")
    @Mapping(target = "state", source = "confirmed")
    ParticipationRequestDto fromRequestEntity(RequestEntity entity);

    @Mapping(target = "category", ignore = true)
    Event fromUpdateAdminEventRequest(UpdateEventAdminRequestDto updateEventAdminRequestDto);

    @Mapping(target = "events", source = "eventEntities")
    CompilationDto fromCompilationEntity(CompilationEntity compilationEntity);

    @Mapping(target = "initiator", source = "owner")
    EventShortDto toEventShortDtoFromEntity(EventEntity eventEntity);

    @Mapping(target = "pinned", defaultValue = "false", source = "pinned")
    CompilationEntity toCompilationEntityFromNewCompilationDto(NewCompilationDto newCompilationDto);
}
