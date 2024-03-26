package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.dto.categoryDtos.CategoryDto;
import ru.practicum.server.dto.categoryDtos.NewCategoryDto;
import ru.practicum.server.dto.compilationDtos.CompilationDto;
import ru.practicum.server.dto.compilationDtos.NewCompilationDto;
import ru.practicum.server.dto.compilationDtos.UpdateCompilationRequest;
import ru.practicum.server.dto.eventDtos.EventFullDto;
import ru.practicum.server.dto.eventDtos.EventShortDto;
import ru.practicum.server.dto.eventDtos.NewEventDto;
import ru.practicum.server.dto.eventDtos.UpdateEventAdminRequestDto;
import ru.practicum.server.dto.requestDtos.EventRequestStatusUpdateDto;
import ru.practicum.server.dto.requestDtos.EventRequestStatusUpdateResult;
import ru.practicum.server.dto.requestDtos.ParticipationRequestDto;
import ru.practicum.server.dto.requestDtos.UpdateEventUserRequestDto;
import ru.practicum.server.enums.RequestStatusEnum;
import ru.practicum.server.enums.StateEnum;
import ru.practicum.server.exceptions.IncorrectDateException;
import ru.practicum.server.exceptions.IncorrectRequestException;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.mappers.EventMapper;
import ru.practicum.server.mappers.UserMapper;
import ru.practicum.server.models.*;
import ru.practicum.server.repository.*;
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
    private final CompilationDB compilationStorage;

    @Override
    public ResponseEntity<?> getByUserId(Long userId, Pageable pageable) {
        List<EventShortDto> eventShortDtosList = eventStorage.getEventByUserId(userId, pageable)
                .stream()
                .map(EventMapper.EVENT_MAPPER::toEventShortDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(eventShortDtosList, HttpStatus.OK);
    }

    @Override
    @Transactional
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
        if (updateEvent.getCategory() != null) {
            event.setCategory(categoryStorage.getCategoryById(updateEvent.getCategory()));
        }
        StateEnum nextState = null;
        if (updateEvent.getStateAction() != null) {
            switch (updateEvent.getStateAction()) {
                case CANCEL_REVIEW:
                    nextState = StateEnum.CANCELED;
                    break;
                case SEND_TO_REVIEW:
                    nextState = StateEnum.PENDING;
                    break;
            }
        }
        EventFullDto eventFullDto = EventMapper.EVENT_MAPPER.toEventFullDto(
                eventStorage.updateEventFromUser(event, userId, eventId, nextState));
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
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        eventRequestStatusUpdateResult.setConfirmedRequest(listToReturn);
        return new ResponseEntity<>(eventRequestStatusUpdateResult, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> createCategory(NewCategoryDto newCategoryDto) {
        Category category = EventMapper.EVENT_MAPPER.fromNewCategoryDto(newCategoryDto);
        CategoryDto categoryDto = EventMapper.EVENT_MAPPER.toCategoryDto(categoryStorage.createCategory(category));
        return new ResponseEntity<>(categoryDto, HttpStatus.CREATED);
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
        List<CategoryDto> categoryDtos = categoryStorage.getPageableCategory(pageable)
                .stream()
                .map(EventMapper.EVENT_MAPPER::toCategoryDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(categoryDtos, HttpStatus.OK);
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
            throw new IncorrectRequestException("Owner didn't send request to event");
        }
        if (!event.getState().equals(StateEnum.PUBLISHED)) {
            throw new IncorrectRequestException("Event must be PUBLISHED");
        }
        if (!event.getParticipantLimit().equals(0) && event.getParticipantLimit() <= event.getCountConfirmedRequests()) {
            throw new IncorrectRequestException("Not enought places");
        }
        if (event.getRequestModeration().equals(false) || event.getParticipantLimit().equals(0)) {
            request.setConfirmed(RequestStatusEnum.CONFIRMED);
        } else {
            request.setConfirmed(RequestStatusEnum.PENDING);
        }
        request.setCreated(LocalDateTime.now());
        ParticipationRequestDto requestDto = EventMapper.EVENT_MAPPER.fromRequestEntity(
                requestStorage.createNewRequest(request));
        return new ResponseEntity<>(requestDto, HttpStatus.CREATED);
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

    @Override
    public ResponseEntity<?> getEvents(AdminFilterParam adminFilterParam) {
        List<Event> events = eventStorage.getEventsByFilter(adminFilterParam);
        List<EventFullDto> eventFullDtos = events.stream()
                .map(EventMapper.EVENT_MAPPER::toEventFullDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(eventFullDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto requestDto) {
        Event event = EventMapper.EVENT_MAPPER.fromUpdateAdminEventRequest(requestDto);
        if (requestDto.getCategory() != null) {
            event.setCategory(categoryStorage.getCategoryById(requestDto.getCategory()));
        }
        StateEnum nextState = null;
        if (requestDto.getStateAction() != null) {
            switch (requestDto.getStateAction()) {
                case REJECT_EVENT:
                    nextState = StateEnum.CANCELED;
                    break;
                case PUBLISH_EVENT:
                    nextState = StateEnum.PUBLISHED;
                    break;
            }
        }
        EventFullDto updateEvent = EventMapper.EVENT_MAPPER.toEventFullDto(
                eventStorage.updateEventFromAdmin(event, eventId, nextState));
        return new ResponseEntity<>(updateEvent, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> createCompilation(NewCompilationDto newCompilationDto) {
        CompilationDto compilationDto = EventMapper.EVENT_MAPPER.fromCompilationEntity(
                compilationStorage.createCompilation(newCompilationDto));
        return new ResponseEntity<>(compilationDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getCompilationById(Long compId) {
        CompilationDto compilationDto = EventMapper.EVENT_MAPPER.fromCompilationEntity(
                compilationStorage.getCompilationById(compId));

        return new ResponseEntity<>(compilationDto, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getPageableCompilation(Boolean pinned, Pageable pageable) {
        List<CompilationDto> compilationDto = compilationStorage.getPageableCompilations(pinned, pageable)
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromCompilationEntity)
                .collect(Collectors.toList());
        return new ResponseEntity<>(compilationDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> removeCompilation(Long id) {
        compilationStorage.removeById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<?> updateCompilation(Long compilationId, UpdateCompilationRequest request) {
        CompilationDto compilationDto = EventMapper.EVENT_MAPPER.fromCompilationEntity(
                compilationStorage.updateCompilation(request, compilationId));
        return new ResponseEntity<>(compilationDto, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getEventsByPublic(PublicFilterParam filterParam) {
        List<EventShortDto> events = eventStorage.getEventsByPublicFilter(filterParam).stream()
                .map(EventMapper.EVENT_MAPPER::toEventShortDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getEventByIdPublic(Long eventId) {
        Event event = eventStorage.getEventByIdAndStatusPublished(eventId);
        return new ResponseEntity<>(EventMapper.EVENT_MAPPER.toEventFullDto(event), HttpStatus.OK);
    }

}
