package ru.practicum.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.server.enums.PrivateStateActionEnum;
import ru.practicum.server.enums.StateEnum;
import ru.practicum.server.exceptions.AlreadyUseException;
import ru.practicum.server.exceptions.IncorrectDateException;
import ru.practicum.server.exceptions.IncorrectRequestException;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.mappers.EventMapper;
import ru.practicum.server.models.Event;
import ru.practicum.server.models.FilterParam;
import ru.practicum.server.repository.entities.EventEntity;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventDB {
    private final EventRepository eventRepository;

    public Event createEvent(Event event) {
        EventEntity eventEntity = EventMapper.EVENT_MAPPER.toEventEntity(event);
        try {
            eventRepository.save(eventEntity);
            return EventMapper.EVENT_MAPPER.fromEventEntity(eventEntity);
        } catch (ConstraintViolationException e) {
            throw new AlreadyUseException("Произошла ошибка при создании");
        }
    }

    public List<Event> getEventByUserId(Long userId, Pageable pageable) {
        return eventRepository.getEventEntitiesByOwnerId(userId, pageable)
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromEventEntity)
                .collect(Collectors.toList());
    }

    public Event getEventByOwnerIdAndEventId(Long userId, Long eventId) {
        Optional<EventEntity> event = eventRepository.getEventEntitiesByOwnerIdAndId(userId, eventId);
        if (event.isPresent()) {
            return EventMapper.EVENT_MAPPER.fromEventEntity(event.get());
        } else {
            throw new NotFoundException(String.format("Event with id=%s not found", eventId));
        }
    }

    public Event updateEvent(Event updateEvent, Long userId, Long eventId, PrivateStateActionEnum actionEnum) {
        Optional<EventEntity> event;
        if (userId != null) {
            event = eventRepository.getEventEntitiesByOwnerIdAndId(userId, eventId);
        } else {
            event = eventRepository.findById(eventId);
        }
        if (event.isPresent()) {
            if (event.get().getState().equals(StateEnum.PUBLISHED)) {
                throw new IncorrectRequestException("Event must not be published");
            } else if (event.get().getEventDate().isAfter(LocalDateTime.now().plusHours(2L))) {
                throw new IncorrectDateException("Дата события не соответствует необходимой для изменения");
            } else {
                EventEntity eventEntity = setDifferentFields(event.get(), updateEvent);
                switch (actionEnum) {
                    case CANCEL_REVIEW:
                        eventEntity.setState(StateEnum.CANCELED);
                        break;
                    case SEND_TO_REVIEW:
                        eventEntity.setState(StateEnum.PENDING);
                        break;
                }
                eventRepository.save(eventEntity);
                return EventMapper.EVENT_MAPPER.fromEventEntity(eventEntity);
            }
        } else {
            throw new NotFoundException(String.format("Event with id=%s not found", eventId));
        }
    }

    public Event getById(Long eventId) {
        Optional<EventEntity> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            return EventMapper.EVENT_MAPPER.fromEventEntity(event.get());
        } else {
            throw new NotFoundException(String.format("Event with id=%s not found", eventId));
        }
    }

    public List<Event> getEventsByFilter(FilterParam filterParam) {
        Specification<EventEntity> specification = Specification
                .where(EventSpecification.ownerSpecification(filterParam))
                .and(EventSpecification.categorySpecification(filterParam))
                .and(EventSpecification.statesSpecification(filterParam))
                .and(EventSpecification.startSpecification(filterParam))
                .and(EventSpecification.endSpecification(filterParam));
        return eventRepository.findAll(specification, filterParam.getPageable()).toList()
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromEventEntity).collect(Collectors.toList());
    }

    private EventEntity setDifferentFields(EventEntity event, Event newEvent) {
        if (newEvent.getAnnotation() != null) {
            event.setAnnotation(newEvent.getAnnotation());
        }
        if (newEvent.getCategory() != null) {
            event.setCategory(EventMapper.EVENT_MAPPER.toCategoryEntity(newEvent.getCategory()));
        }
        if (newEvent.getDescription() != null) {
            event.setDescription(event.getDescription());
        }
        if (newEvent.getEventDate() != null) {
            if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
                throw new IncorrectDateException("data");
            } else {
                event.setEventDate(newEvent.getEventDate());
            }
        }
        if (newEvent.getPaid() != null) {
            event.setPaid(newEvent.getPaid());
        }
        if (newEvent.getParticipantLimit() != null) {
            event.setLimit(newEvent.getParticipantLimit());
        }
        if (newEvent.getRequestModeration() != null) {
            event.setModeration(newEvent.getRequestModeration());
        }
        if (newEvent.getTitle() != null) {
            event.setTitle(newEvent.getTitle());
        }
        return event;
    }


}
