package ru.practicum.server.controllers.privateControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.requestDtos.EventRequestStatusUpdateDto;
import ru.practicum.server.dto.eventDtos.NewEventDto;
import ru.practicum.server.dto.requestDtos.UpdateEventUserRequestDto;
import ru.practicum.server.service.interfaces.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping("/{userId}/events")
    ResponseEntity<?> getByUserId(@Valid @PathVariable(name = "userId") Long userId,
                                  @Nullable @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @Nullable @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Has new request to get events by user with id {}", userId);
        return eventService.getByUserId(userId, pageable);
    }

    @PostMapping("/{userId}/events")
    ResponseEntity<?> createNewEvent(@Valid @RequestBody NewEventDto newEventDto,
                                     @PathVariable(name = "userId") Long userId) {
        log.info("Has new request to create event with title {} from user with id {}", newEventDto.getTitle(), userId);
        return eventService.createNewEvent(newEventDto, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    ResponseEntity<?> getEventById(@Positive @PathVariable(name = "userId") Long userId,
                                   @Positive @PathVariable(name = "eventId") Long eventId) {
        log.info("Has new request to get event {} from user with id {}", eventId, userId);
        return eventService.getByEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    ResponseEntity<?> updateEvent(@Valid @RequestBody UpdateEventUserRequestDto updateEventUserRequestDto,
                                  @PathVariable(name = "userId") Long userId,
                                  @PathVariable(name = "eventId") Long eventId) {
        log.info("Has new request to update event with id {} from user with id {}", eventId, userId);
        return eventService.updateEvent(updateEventUserRequestDto, userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    ResponseEntity<?> getRequestsByEventId(@PathVariable(name = "userId") Long userId,
                                           @PathVariable(name = "eventId") Long eventId) {
        log.info("Has new request to get requests by event id {} from user with id {}", eventId, userId);
        return eventService.getRequestByEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    ResponseEntity<?> updateRequestStatus(@Valid @RequestBody EventRequestStatusUpdateDto eventRequestStatusUpdateDto,
                                        @PathVariable(name = "userId") Long userId,
                                        @PathVariable(name = "eventId") Long eventId) {
        log.info("Has new request to update request status for event with id {}", eventId);
        return eventService.changeStatusForEvent(eventRequestStatusUpdateDto, userId, eventId);
    }
}
