package ru.practicum.server.controllers.privateControllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.service.interfaces.EventService;

import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class PrivateRequestsController {

    private final EventService eventService;

    @GetMapping("/{userId}/requests")
    ResponseEntity<?> getRequests(@PositiveOrZero @PathVariable(name = "userId") Long userId) {
        return eventService.getRequestByUser(userId);
    }

    @PostMapping("/{userId}/requests")
    ResponseEntity<?> createRequest(@PositiveOrZero @PathVariable(name = "userId") Long userId,
                                    @RequestParam(name = "eventId") Long eventId) {
        return eventService.createRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    ResponseEntity<?> cancelRequest(@PositiveOrZero @PathVariable(name = "userId") Long userId,
                                    @PositiveOrZero @PathVariable(name = "requestId") Long requestId) {
        return eventService.removeRequest(requestId, userId);
    }
}
