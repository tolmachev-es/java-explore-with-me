package ru.practicum.server.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.server.dto.*;
import ru.practicum.server.models.Category;
import ru.practicum.server.models.Event;
import ru.practicum.server.repository.entities.CategoryEntity;
import ru.practicum.server.repository.entities.CompilationEntity;
import ru.practicum.server.repository.entities.EventEntity;
import ru.practicum.server.repository.entities.RequestEntity;

import java.time.LocalDateTime;

@Mapper(imports = {LocalDateTime.class})
public interface EventMapper {
    EventMapper EVENT_MAPPER = Mappers.getMapper(EventMapper.class);

    Category fromCategoryEntity(CategoryEntity categoryEntity);

    CategoryEntity toCategoryEntity(Category category);

    Category fromCategoryDto(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);

    Category fromNewCategoryDto(NewCategoryDto newCategoryDto);

    @Mapping(target = "participantLimit", source = "limit")
    @Mapping(target = "requestModeration", source = "moderation")
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
    EventEntity toEventEntity(Event event);

    @Mapping(target = "initiator", source = "owner")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "initiator", source = "owner")
    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "category", ignore = true)
    Event fromUpdateEventRequest(UpdateEventUserRequestDto updateEventUserRequestDto);

    @Mapping(target = "eventId", expression = "java(entity.getEventId().getId())")
    @Mapping(target = "requester", expression = "java(entity.getUserId().getId())")
    @Mapping(target = "state", expression = "java(entity.getEventId().getState())")
    ParticipationRequestDto fromRequestEntity(RequestEntity entity);

    @Mapping(target = "category", ignore = true)
    Event fromUpdateAdminEventRequest(UpdateEventAdminRequestDto updateEventAdminRequestDto);

    @Mapping(target = "events", source = "eventEntities")
    CompilationDto fromCompilationEntity(CompilationEntity compilationEntity);

    @Mapping(target = "initiator", source = "owner")
    EventShortDto toEventShortDtoFromEntity(EventEntity eventEntity);

    CompilationEntity toCompilationEntityFromNewCompilationDto(NewCompilationDto newCompilationDto);
}
