package ru.practicum.server.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.client.models.ViewStatsDto;
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
import ru.practicum.server.mappers.EventMapper;
import ru.practicum.server.mappers.UserMapper;
import ru.practicum.server.models.*;
import ru.practicum.server.repository.*;
import ru.practicum.server.repository.entities.RequestEntity;
import ru.practicum.server.service.interfaces.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final CategoryDao categoryStorage;
    private final EventDao eventStorage;
    private final UserDao userStorage;
    private final RequestDao requestStorage;
    private final CompilationDao compilationStorage;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getByUserId(Long userId, Pageable pageable) {
        return eventStorage.getEventByUserId(userId, pageable)
                .stream()
                .map(EventMapper.EVENT_MAPPER::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto createNewEvent(NewEventDto newEventDto, Long userid) {
        User user = userStorage.getUserById(userid);
        Event event = EventMapper.EVENT_MAPPER.fromNewEventDto(newEventDto);
        event.setCategory(categoryStorage.getCategoryById(newEventDto.getCategory()));
        event.setOwner(user);
        event.setViews(0);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new IncorrectDateException("Должно содержать дату, которая еще не наступила");
        } else {
            Event createEvent = eventStorage.createEvent(event);
            return EventMapper.EVENT_MAPPER.toEventFullDto(createEvent);
        }
    }

    @Override
    public EventFullDto getByEventId(Long userId, Long eventId) {
        Event event = eventStorage.getEventByOwnerIdAndEventId(userId, eventId);
        return EventMapper.EVENT_MAPPER.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(UpdateEventUserRequestDto updateEvent,
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
        return EventMapper.EVENT_MAPPER.toEventFullDto(
                eventStorage.updateEventFromUser(event, userId, eventId, nextState));
    }

    @Override
    public List<ParticipationRequestDto> getRequestByEvent(Long userId, Long eventId) {
        return requestStorage.getRequestsByEvent(eventId)
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromRequestEntity)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult changeStatusForEventRequests(EventRequestStatusUpdateDto requestStatusUpdateDto,
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
        EventRequestStatusUpdateResult eventRequestStatusUpdateRequest = new EventRequestStatusUpdateResult();
        for (ParticipationRequestDto requestDto : listToReturn) {
            if (requestDto.getState().equals(RequestStatusEnum.CONFIRMED)) {
                eventRequestStatusUpdateRequest.getConfirmedRequests().add(requestDto);
            } else {
                eventRequestStatusUpdateRequest.getRejectedRequests().add(requestDto);
            }
        }
        return eventRequestStatusUpdateRequest;
    }

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = EventMapper.EVENT_MAPPER.fromNewCategoryDto(newCategoryDto);
        return EventMapper.EVENT_MAPPER.toCategoryDto(categoryStorage.createCategory(category));
    }

    @Override
    public void removeCategory(Long categoryId) {
        categoryStorage.deleteCategory(categoryId);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        return EventMapper.EVENT_MAPPER.toCategoryDto(
                categoryStorage.updateCategory(categoryDto, categoryId));
    }

    @Override
    public List<CategoryDto> getPageableCategory(Pageable pageable) {
        return categoryStorage.getPageableCategory(pageable)
                .stream()
                .map(EventMapper.EVENT_MAPPER::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return EventMapper.EVENT_MAPPER.toCategoryDto(categoryStorage.getCategoryById(categoryId));
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        RequestEntity request = new RequestEntity();
        request.setCreated(LocalDateTime.now());
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
        return EventMapper.EVENT_MAPPER.fromRequestEntity(requestStorage.createNewRequest(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestByUser(Long userId) {
        return requestStorage.getByUserId(userId)
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromRequestEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto removeRequest(Long requestId, Long userId) {
        return EventMapper.EVENT_MAPPER.fromRequestEntity(requestStorage.removeRequest(requestId, userId));
    }

    @Override
    public List<EventFullDto> getEvents(AdminFilterParam adminFilterParam) {
        List<Event> events = eventStorage.getEventsByFilter(adminFilterParam);
        return events.stream()
                .map(EventMapper.EVENT_MAPPER::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto requestDto) {
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
        return EventMapper.EVENT_MAPPER.toEventFullDto(eventStorage.updateEventFromAdmin(event, eventId, nextState));
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        return EventMapper.EVENT_MAPPER.fromCompilationEntity(compilationStorage.createCompilation(newCompilationDto));
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        return EventMapper.EVENT_MAPPER.fromCompilationEntity(compilationStorage.getCompilationById(compId));
    }

    @Override
    public List<CompilationDto> getPageableCompilation(Boolean pinned, Pageable pageable) {
        return compilationStorage.getPageableCompilations(pinned, pageable)
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromCompilationEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void removeCompilation(Long id) {
        compilationStorage.removeById(id);
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest request) {
        return EventMapper.EVENT_MAPPER.fromCompilationEntity(
                compilationStorage.updateCompilation(request, compilationId));
    }

    @Override
    public List<EventShortDto> getEventsByPublic(PublicFilterParam filterParam, HttpServletRequest httpServletRequest) {
        statsClient.addNewHit("main-service", httpServletRequest);
        return addStatistic(eventStorage.getEventsByPublicFilter(filterParam))
                .stream()
                .map(EventMapper.EVENT_MAPPER::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest httpServletRequest) {
        statsClient.addNewHit("main-service", httpServletRequest);
        List<Event> events = addStatistic(List.of(eventStorage.getEventByIdAndStatusPublished(eventId)));
        if (events.size() == 1) {
            return EventMapper.EVENT_MAPPER.toEventFullDto(events.get(0));
        } else {
            throw new IncorrectRequestException("Something went wrong");
        }
    }

    private List<Event> addStatistic(List<Event> events) {
        Pattern pattern = Pattern.compile("/events/(\\d+)$");

        List<String> newUris = events.stream()
                .map(s -> String.format("/events/%s", s.getId()))
                .collect(Collectors.toList());
        LocalDateTime startTime = events.stream().min(new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return o1.getCreatedOn().compareTo(o2.getCreatedOn());
            }
        }).get().getCreatedOn();

        List<ViewStatsDto> viewStatsDtos = statsClient.getStats(startTime, LocalDateTime.now(), newUris, true);
        Map<Long, Integer> statistic = new HashMap<>();
        for (ViewStatsDto viewStatsDto : viewStatsDtos) {
            Matcher matcher = pattern.matcher(viewStatsDto.getUri());
            if (matcher.find()) {
                String id = matcher.group(1);
                statistic.put(Long.parseLong(id), viewStatsDto.getHits());
            }

        }
        for (Event event : events) {
            event.setViews(statistic.get(event.getId()));
        }
        return events;
    }
}
