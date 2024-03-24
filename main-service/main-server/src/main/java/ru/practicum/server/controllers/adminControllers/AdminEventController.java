package ru.practicum.server.controllers.adminControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.UpdateEventAdminRequestDto;
import ru.practicum.server.enums.RequestStatusEnum;
import ru.practicum.server.models.AdminFilterParam;
import ru.practicum.server.service.interfaces.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminEventController {
    private final EventService eventService;

    @GetMapping("/events")
    ResponseEntity<?> getEvents(@RequestParam(name = "users") List<Long> users,
                                @RequestParam(name = "states") List<String> states,
                                @RequestParam(name = "categories") List<Long> categories,
                                @RequestParam(name = "rangeStart") LocalDateTime rangeStart,
                                @RequestParam(name = "rangeEnd") LocalDateTime rangeEnd,
                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        AdminFilterParam adminFilterParam = AdminFilterParam.builder()
                .users(users)
                .states(states.stream().map(RequestStatusEnum::valueOf).collect(Collectors.toList()))
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .pageable(PageRequest.of(from / size, size))
                .build();
        log.info("Has new request for search events with parameters {}", adminFilterParam.toString());
        return eventService.getEvents(adminFilterParam);
    }

    @PatchMapping("/{eventId}")
    ResponseEntity<?> updateEvent(@Valid @RequestBody UpdateEventAdminRequestDto requestDto,
                                  @PathVariable(name = "eventId") Long eventId) {
        log.info("Has new request to update event with id {}", eventId);
        return eventService.updateEventByAdmin(eventId, requestDto);
    }
}
