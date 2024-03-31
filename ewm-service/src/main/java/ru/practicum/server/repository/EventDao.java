package ru.practicum.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.enums.StateEnum;
import ru.practicum.server.exceptions.*;
import ru.practicum.server.mappers.EventMapper;
import ru.practicum.server.models.AdminFilterParam;
import ru.practicum.server.models.Event;
import ru.practicum.server.models.PublicFilterParam;
import ru.practicum.server.repository.entities.EventEntity;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventDao {
    private final EventRepository eventRepository;

    @Transactional
    public Event createEvent(Event event) {
        EventEntity eventEntity = EventMapper.EVENT_MAPPER.toEventEntity(event);
        try {
            eventRepository.save(eventEntity);
            return getById(eventEntity.getId());
        } catch (ConstraintViolationException e) {
            throw new AlreadyUseException("Произошла ошибка при создании");
        }
    }

    @Transactional
    public List<Event> getEventByUserId(Long userId, Pageable pageable) {
        return eventRepository.getEventEntitiesByOwnerId(userId, pageable)
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromEventEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public Event getEventByOwnerIdAndEventId(Long userId, Long eventId) {
        Optional<EventEntity> event = eventRepository.getEventEntitiesByOwnerIdAndId(userId, eventId);
        if (event.isPresent()) {
            return EventMapper.EVENT_MAPPER.fromEventEntity(event.get());
        } else {
            throw new NotFoundException(String.format("Event with id=%s not found", eventId));
        }
    }

    @Transactional
    public Event updateEventFromUser(Event updateEvent, Long userId, Long eventId, StateEnum stateEnum) {
        Optional<EventEntity> event = eventRepository.getEventEntitiesByOwnerIdAndId(userId, eventId);
        if (event.isPresent()) {
            if (event.get().getState().equals(StateEnum.PUBLISHED)) {
                throw new IncorrectRequestException("Event must not be published");
            } else if (event.get().getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
                throw new IncorrectDateException("Дата события не соответствует необходимой для изменения");
            } else {
                EventEntity eventEntity = setDifferentFields(event.get(), updateEvent, stateEnum);
                eventRepository.save(eventEntity);
                return EventMapper.EVENT_MAPPER.fromEventEntity(eventEntity);
            }
        } else {
            throw new NotFoundException(String.format("Event with id=%s not found", eventId));
        }
    }

    @Transactional
    public Event updateEventFromAdmin(Event updateEvent, Long eventId, StateEnum stateEnum) {
        Optional<EventEntity> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            if (!event.get().getState().equals(StateEnum.PENDING)) {
                throw new IncorrectRequestException("Event must be PENDING");
            } else if (event.get().getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
                throw new IncorrectDateException("Дата события не соответствует необходимой для изменения");
            } else {
                EventEntity eventEntity = setDifferentFields(event.get(), updateEvent, stateEnum);
                eventRepository.save(eventEntity);
                return EventMapper.EVENT_MAPPER.fromEventEntity(eventEntity);
            }
        } else {
            throw new NotFoundException(String.format("Event with id=%s not found", eventId));
        }
    }

    @Transactional
    public Event getById(Long eventId) {
        Optional<EventEntity> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            return EventMapper.EVENT_MAPPER.fromEventEntity(event.get());
        } else {
            throw new NotFoundException(String.format("Event with id=%s not found", eventId));
        }
    }

    @Transactional
    public List<Event> getEventsByFilter(AdminFilterParam adminFilterParam) {
        Specification<EventEntity> specification = EventSpecificationBuilder
                .getEventSpecificationByAdminFilterParam(adminFilterParam);
        return eventRepository.findAll(specification, adminFilterParam.getPageable()).toList()
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromEventEntity).collect(Collectors.toList());
    }

    @Transactional
    public List<Event> getEventsByPublicFilter(PublicFilterParam publicFilterParam) {
        if (publicFilterParam.getEnd() != null && publicFilterParam.getStart() != null) {
            if (publicFilterParam.getEnd().isBefore(publicFilterParam.getStart())) {
                throw new IncorrectSearchDate("End date before start date");
            }
        }
        Sort sort = null;
        if (publicFilterParam.getSort() == null) {
            sort = Sort.by("id");
        } else {
            switch (publicFilterParam.getSort()) {
                case VIEWS:
                    sort = Sort.by("views");
                    break;
                case EVENT_DATE:
                    sort = Sort.by("eventDate");
                    break;
            }
        }
        Pageable pageable = PageRequest.of(publicFilterParam.getFrom() / publicFilterParam.getSize(),
                publicFilterParam.getSize(), sort);
        Specification<EventEntity> specification = EventSpecificationBuilder
                .getEventSpecificationByPublicFilterParam(publicFilterParam);
        return eventRepository.findAll(specification, pageable)
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromEventEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public Event getEventByIdAndStatusPublished(Long eventId) {
        Optional<EventEntity> eventEntity = eventRepository.getEventEntityByIdAndState(eventId, StateEnum.PUBLISHED);
        if (eventEntity.isPresent()) {
            EventEntity event = eventEntity.get();
            event.setViews(event.getViews() + 1);
            eventRepository.save(eventEntity.get());
            return EventMapper.EVENT_MAPPER.fromEventEntity(event);
        } else {
            throw new NotFoundException(String.format("Event with id %s not found", eventId));
        }
    }

    private EventEntity setDifferentFields(EventEntity event, Event newEvent, StateEnum stateEnum) {
        if (stateEnum != null) {
            if (stateEnum == StateEnum.PUBLISHED) {
                event.setPublishedOn(LocalDateTime.now());
            }
            event.setState(stateEnum);
        }
        if (newEvent.getAnnotation() != null) {
            event.setAnnotation(newEvent.getAnnotation());
        }
        if (newEvent.getCategory() != null) {
            event.setCategory(EventMapper.EVENT_MAPPER.toCategoryEntity(newEvent.getCategory()));
        }
        if (newEvent.getDescription() != null) {
            event.setDescription(newEvent.getDescription());
        }
        if (newEvent.getEventDate() != null) {
            if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
                throw new IncorrectDateException("Event with this date does not update");
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
