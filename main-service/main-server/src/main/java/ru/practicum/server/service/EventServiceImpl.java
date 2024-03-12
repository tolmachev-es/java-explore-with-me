package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.server.dto.*;
import ru.practicum.server.enums.RequestStatusEnum;
import ru.practicum.server.enums.StateEnum;
import ru.practicum.server.exceptions.IncorrectDateException;
import ru.practicum.server.mappers.EventMapper;
import ru.practicum.server.mappers.UserMapper;
import ru.practicum.server.models.Category;
import ru.practicum.server.models.Event;
import ru.practicum.server.models.User;
import ru.practicum.server.repository.CategoryDB;
import ru.practicum.server.repository.EventDB;
import ru.practicum.server.repository.RequestDB;
import ru.practicum.server.repository.UserDB;
import ru.practicum.server.repository.entities.RequestEntity;
import ru.practicum.server.service.interfaces.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final CategoryDB categoryStorage;
    private final EventDB eventStorage;
    private final UserDB userStorage;
    private final RequestDB requestStorage;

    @Override
    public ResponseEntity<?> getByUserId(Long userId, Pageable pageable) {
        List<EventShortDto> eventShortDtosList = eventStorage.getEventByUserId(userId, pageable)
                .stream()
                .map(EventMapper.EVENT_MAPPER::toEventShortDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(eventShortDtosList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> createNewEvent(NewEventDto newEventDto, Long userid) {
        User user = userStorage.getUserById(userid);
        Event event = EventMapper.EVENT_MAPPER.fromNewEventDto(newEventDto);
        event.setCategory(categoryStorage.getCategoryById(newEventDto.getCategory()));
        event.setOwner(user);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new IncorrectDateException("Должно содержать дату, которая еще не наступила");
        } else {
            Event createEvent = eventStorage.createEvent(event);
            EventFullDto eventFullDto = EventMapper.EVENT_MAPPER.toEventFullDto(createEvent);
            return new ResponseEntity<>(eventFullDto, HttpStatus.CREATED);
        }
    }

    @Override
    public ResponseEntity<?> getByEventId(Long userId, Long eventId) {
        Event event = eventStorage.getEventByOwnerIdAndEventId(userId, eventId);
        EventFullDto eventFullDto = EventMapper.EVENT_MAPPER.toEventFullDto(event);
        return new ResponseEntity<>(eventFullDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateEvent(UpdateEventUserRequestDto updateEvent,
                                         Long userId,
                                         Long eventId) {
        Event event = EventMapper.EVENT_MAPPER.fromUpdateEventRequest(updateEvent);
        event.setCategory(categoryStorage.getCategoryById(updateEvent.getCategory()));
        EventFullDto eventFullDto = EventMapper.EVENT_MAPPER.toEventFullDto(
                eventStorage.updateEvent(event, userId, eventId, updateEvent.getStateAction()));
        return new ResponseEntity<>(eventFullDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getRequestByEvent(Long userId, Long eventId) {
        List<ParticipationRequestDto> dtos = requestStorage.getRequestsByEvent(eventId)
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromRequestEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> changeStatusForEvent(EventRequestStatusUpdateDto requestStatusUpdateDto,
                                                  Long userId,
                                                  Long eventId) {
        Event event = eventStorage.getById(eventId);
        List<RequestEntity> resultList = new ArrayList<>();
        if (!event.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Incorrect owner");
        } else {
            switch (requestStatusUpdateDto.getStatus()) {
                case CONFIRMED:
                    if (event.getRequestModeration()) {
                        resultList = requestStorage.requestConfirmStatus(
                                requestStatusUpdateDto.getRequestIds(),
                                event.getParticipantLimit(),
                                eventId);
                    } else {
                        resultList = requestStorage.getByIds(requestStatusUpdateDto.getRequestIds());
                    }
                    break;
                case REJECTED:
                    resultList = requestStorage.requestRejectStatus(requestStatusUpdateDto.getRequestIds());
            }
        }
        List<ParticipationRequestDto> listToReturn = resultList.stream()
                .map(EventMapper.EVENT_MAPPER::fromRequestEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(listToReturn, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> createCategory(NewCategoryDto newCategoryDto) {
        Category category = EventMapper.EVENT_MAPPER.fromNewCategoryDto(newCategoryDto);
        CategoryDto categoryDto = EventMapper.EVENT_MAPPER.toCategoryDto(categoryStorage.createCategory(category));
        return new ResponseEntity<>(categoryDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> removeCategory(Long categoryId) {
        categoryStorage.deleteCategory(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<?> updateCategory(CategoryDto categoryDto, Long categoryId) {
        CategoryDto category = EventMapper.EVENT_MAPPER.toCategoryDto(
                categoryStorage.updateCategory(categoryDto, categoryId));
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getPageableCategory(Pageable pageable) {
        return null;
    }

    @Override
    public ResponseEntity<?> getCategoryById(Long categoryId) {
        CategoryDto categoryDto = EventMapper.EVENT_MAPPER.toCategoryDto(categoryStorage.getCategoryById(categoryId));
        return new ResponseEntity<>(categoryDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> createRequest(Long userId, Long eventId) {
        RequestEntity request = new RequestEntity();
        User user = userStorage.getUserById(userId);
        request.setUserId(UserMapper.USER_MAPPER.toEntity(user));
        Event event = eventStorage.getById(eventId);
        request.setEventId(EventMapper.EVENT_MAPPER.toEventEntity(event));
        if (event.getOwner().getId().equals(userId)) {
            throw new RuntimeException("event service impl create request");
        }
        if (event.getState().equals(StateEnum.PUBLISHED)) {
            throw new RuntimeException("event service impl create req 2");
        }
        if (event.getParticipantLimit() > requestStorage.countGuest(eventId)) {
            throw new RuntimeException("Count exception");
        }
        if (event.getRequestModeration().equals(false) && event.getParticipantLimit().equals(0)) {
            request.setConfirmed(RequestStatusEnum.ALLOWS);
        }
        request.setCreated(LocalDateTime.now());
        ParticipationRequestDto requestDto = EventMapper.EVENT_MAPPER.fromRequestEntity(
                requestStorage.createNewRequest(request));
        return new ResponseEntity<>(requestDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getRequestByUser(Long userId) {
        List<ParticipationRequestDto> dtos = requestStorage.getByUserId(userId)
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromRequestEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> removeRequest(Long requestId, Long userId) {
        requestStorage.removeRequest(requestId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
