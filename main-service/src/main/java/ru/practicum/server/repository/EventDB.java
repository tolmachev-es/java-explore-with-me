package ru.practicum.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.enums.RequestStatusEnum;
import ru.practicum.server.enums.StateEnum;
import ru.practicum.server.exceptions.AlreadyUseException;
import ru.practicum.server.exceptions.IncorrectDateException;
import ru.practicum.server.exceptions.IncorrectRequestException;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.mappers.EventMapper;
import ru.practicum.server.models.AdminFilterParam;
import ru.practicum.server.models.Event;
import ru.practicum.server.models.PublicFilterParam;
import ru.practicum.server.repository.entities.EventEntity;
import ru.practicum.server.repository.entities.RequestEntity;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventDB {
    private final EventRepository eventRepository;

    private static Specification<EventEntity> ownerSpecification(List<Long> users) {
        if (users.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(root.get("owner").in(users));
    }

    private static Specification<EventEntity> categorySpecification(List<Long> categories) {
        if (categories == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(root.get("category").in(categories));
    }

    private static Specification<EventEntity> statesSpecification(List<StateEnum> states) {
        if (states.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(root.get("state").in(states));
    }

    private static Specification<EventEntity> startSpecification(LocalDateTime start) {
        if (start == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"),
                start);
    }

    private static Specification<EventEntity> endSpecification(LocalDateTime end) {
        if (end == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"),
                end);
    }

    private static Specification<EventEntity> textSpecification(String text) {
        if (text == null) {
            return null;
        }

        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("annotation"), "%" + text + "%"));
    }

    private static Specification<EventEntity> paidSpecification(Boolean paid) {
        if (paid == null) {
            return null;
        }
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid));
    }

    private static Specification<EventEntity> availableSpecification(Boolean available) {
        if (available == null || !available) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<RequestEntity> subRoot = subquery.from(RequestEntity.class);
            Predicate joinCondition = criteriaBuilder.equal(root.get("ID"), subRoot.get("EVENT_ID"));
            Predicate whereCondition = criteriaBuilder.equal(subRoot.get("CONFIRMED"), true);
            subquery.where(whereCondition, joinCondition);
            Predicate mainQueryCondition = criteriaBuilder.exists(subquery);
            query.where(mainQueryCondition);
            return null;
        };
    }
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

    public Event updateEvent(Event updateEvent, Long userId, Long eventId, StateEnum stateEnum) {
        Optional<EventEntity> event;
        if (userId != null) {
            event = eventRepository.getEventEntitiesByOwnerIdAndId(userId, eventId);
        } else {
            event = eventRepository.findById(eventId);
        }
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

    public Event getById(Long eventId) {
        Optional<EventEntity> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            return EventMapper.EVENT_MAPPER.fromEventEntity(event.get());
        } else {
            throw new NotFoundException(String.format("Event with id=%s not found", eventId));
        }
    }

    public List<Event> getEventsByFilter(AdminFilterParam adminFilterParam) {
        Specification<EventEntity> specification = Specification
                .where(ownerSpecification(adminFilterParam.getUsers()))
                .and(categorySpecification(adminFilterParam.getCategories()))
                .and(statesSpecification(adminFilterParam.getStates()))
                .and(startSpecification(adminFilterParam.getRangeStart()))
                .and(endSpecification(adminFilterParam.getRangeEnd()));
        return eventRepository.findAll(specification, adminFilterParam.getPageable()).toList()
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromEventEntity).collect(Collectors.toList());
    }

    public List<Event> getEventsByPublicFilter(PublicFilterParam publicFilterParam) {
        Sort sort = Sort.by("ID");
        if (publicFilterParam.getSort() != null) {
            sort = Sort.by(String.valueOf(publicFilterParam.getSort()));
        }
        Specification<EventEntity> specification = Specification
                .where(textSpecification(publicFilterParam.getText()))
                .and(categorySpecification(publicFilterParam.getCategories()))
                .and(paidSpecification(publicFilterParam.getPaid()))
                .and(availableSpecification(publicFilterParam.getAvailable()))
                .and((publicFilterParam.getEnd() == null || publicFilterParam.getStart() == null) ?
                        startSpecification(LocalDateTime.now()) : startSpecification(publicFilterParam.getStart()))
                .and((publicFilterParam.getStart() == null || publicFilterParam.getEnd() == null) ? null :
                        endSpecification(publicFilterParam.getEnd()));
        return eventRepository.findAll(specification, publicFilterParam.getPageable())
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromEventEntity)
                .collect(Collectors.toList());
    }

    public Event getEventByIdAndStatusPublished(Long eventId) {
        Optional<EventEntity> eventEntity = eventRepository.getEventEntityByIdAndState(eventId, StateEnum.PUBLISHED);
        if (eventEntity.isPresent()) {
            return EventMapper.EVENT_MAPPER.fromEventEntity(eventEntity.get());
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
