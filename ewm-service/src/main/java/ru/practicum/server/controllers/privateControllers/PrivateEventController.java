package ru.practicum.server.controllers.privateControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.eventDtos.EventFullDto;
import ru.practicum.server.dto.eventDtos.EventShortDto;
import ru.practicum.server.dto.eventDtos.NewEventDto;
import ru.practicum.server.dto.requestDtos.EventRequestStatusUpdateDto;
import ru.practicum.server.dto.requestDtos.EventRequestStatusUpdateResult;
import ru.practicum.server.dto.requestDtos.ParticipationRequestDto;
import ru.practicum.server.dto.requestDtos.UpdateEventUserRequestDto;
import ru.practicum.server.service.interfaces.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<EventShortDto>> getByUserId(@Valid @PathVariable(name = "userId") Long userId,
                                                           @Nullable @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                           @Nullable @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Has new request to get events by user with id {}", userId);
        return new ResponseEntity<>(eventService.getByUserId(userId, pageable), HttpStatus.OK);
    }

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> createNewEvent(@Valid @RequestBody NewEventDto newEventDto,
                                                       @PathVariable(name = "userId") Long userId) {
        log.info("Has new request to create event with title {} from user with id {}", newEventDto.getTitle(), userId);
        return new ResponseEntity<>(eventService.createNewEvent(newEventDto, userId), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getEventById(@Positive @PathVariable(name = "userId") Long userId,
                                                     @Positive @PathVariable(name = "eventId") Long eventId) {
        log.info("Has new request to get event {} from user with id {}", eventId, userId);
        return new ResponseEntity<>(eventService.getByEventId(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@Valid @RequestBody UpdateEventUserRequestDto updateEventUserRequestDto,
                                                    @PathVariable(name = "userId") Long userId,
                                                    @PathVariable(name = "eventId") Long eventId) {
        log.info("Has new request to update event with id {} from user with id {}", eventId, userId);
        return new ResponseEntity<>(eventService.updateEvent(updateEventUserRequestDto, userId, eventId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsByEventId(@PathVariable(name = "userId") Long userId,
                                                                              @PathVariable(name = "eventId") Long eventId) {
        log.info("Has new request to get requests by event id {} from user with id {}", eventId, userId);
        return new ResponseEntity<>(eventService.getRequestByEvent(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(@Valid @RequestBody EventRequestStatusUpdateDto eventRequestStatusUpdateDto,
                                                                              @PathVariable(name = "userId") Long userId,
                                                                              @PathVariable(name = "eventId") Long eventId) {
        log.info("Has new request to update request status for event with id {}", eventId);
        return new ResponseEntity<>(eventService.changeStatusForEventRequests(eventRequestStatusUpdateDto, userId, eventId), HttpStatus.OK);
    }
}
