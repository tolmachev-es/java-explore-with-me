package ru.practicum.server.controllers.privateControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.userDtos.UserDto;
import ru.practicum.server.service.interfaces.UserService;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SubscriberController {

    private final UserService userService;

    @PostMapping("/{userId}/request/{curatorId}")
    public ResponseEntity<Void> subscription(@PathVariable(name = "userId") Long userId,
                                             @PathVariable(name = "curatorId") Long curatorId) {
        log.info("Has new request to subscribe user {} for curator {}", userId, curatorId);
        userService.subscribe(userId, curatorId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}/request/{curatorId}")
    public ResponseEntity<Void> unsubscription(@PathVariable(name = "userId") Long userId,
                                               @PathVariable(name = "curatorId") Long curatorId) {
        log.info("Has new request to delete subscribe from user {} for curator {}", userId, curatorId);
        userService.unsubscription(userId, curatorId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserDto>> getSubscription(@PathVariable(name = "userId") Long userId,
                                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return new ResponseEntity<>(userService.getSubsctiption(userId, PageRequest.of(from / size, size)), HttpStatus.OK);
    }
}
