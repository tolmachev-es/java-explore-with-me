package ru.practicum.server.controllers.privateControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.requestDtos.ParticipationRequestDto;
import ru.practicum.server.service.interfaces.EventService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateRequestsController {

    private final EventService eventService;

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PositiveOrZero @PathVariable(name = "userId") Long userId) {
        log.info("Has new request to get a request from user with name {}", userId);
        return new ResponseEntity<>(eventService.getRequestByUser(userId), HttpStatus.OK);
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> createRequest(@PositiveOrZero @PathVariable(name = "userId") Long userId,
                                                                 @RequestParam(name = "eventId") Long eventId) {
        log.info("Has new request to create a requests for an event with id {} from user with id {}", eventId, userId);
        return new ResponseEntity<>(eventService.createRequest(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PositiveOrZero @PathVariable(name = "userId") Long userId,
                                                                 @PositiveOrZero @PathVariable(name = "requestId") Long requestId) {
        log.info("Has new request to cancel request with id {} from user with id {}", requestId, userId);
        return new ResponseEntity<>(eventService.removeRequest(requestId, userId), HttpStatus.OK);
    }
}
