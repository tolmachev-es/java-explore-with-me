package ru.practicum.server.controllers.privateControllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.EventRequestStatusUpdateDto;
import ru.practicum.server.dto.NewEventDto;
import ru.practicum.server.dto.UpdateEventUserRequestDto;
import ru.practicum.server.service.interfaces.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping("/{userId}/events")
    ResponseEntity<?> getByUserId(@Valid @PathVariable(name = "userId") Long userId,
                                  @Nullable @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @Nullable @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                  HttpServletRequest request) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventService.getByUserId(userId, pageable);
    }

    @PostMapping("/{userId}/events")
    ResponseEntity<?> createNewEvent(@Valid @RequestBody NewEventDto newEventDto,
                                     @PathVariable(name = "userId") Long userId,
                                     HttpServletRequest request) {
        return eventService.createNewEvent(newEventDto, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    ResponseEntity<?> getEventById(@Positive @PathVariable(name = "userId") Long userId,
                                   @Positive @PathVariable(name = "eventId") Long eventId,
                                   HttpServletRequest request) {
        return eventService.getByEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    ResponseEntity<?> updateEvent(@Valid @RequestBody UpdateEventUserRequestDto updateEventUserRequestDto,
                                  @PathVariable(name = "userId") Long userId,
                                  @PathVariable(name = "eventId") Long eventId) {
        return eventService.updateEvent(updateEventUserRequestDto, userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    ResponseEntity<?> getRequestsByEventId(@PathVariable(name = "userId") Long userId,
                                           @PathVariable(name = "eventId") Long eventId) {
        return eventService.getRequestByEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    ResponseEntity<?> updateEventStatus(@Valid @RequestBody EventRequestStatusUpdateDto eventRequestStatusUpdateDto,
                                        @PathVariable(name = "userId") Long userId,
                                        @PathVariable(name = "eventId") Long eventId) {
        return eventService.changeStatusForEvent(eventRequestStatusUpdateDto, userId, eventId);
    }
}
