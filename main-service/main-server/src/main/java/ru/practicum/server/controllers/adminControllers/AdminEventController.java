package ru.practicum.server.controllers.adminControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.UpdateEventAdminRequestDto;
import ru.practicum.server.enums.RequestStatusEnum;
import ru.practicum.server.enums.StateEnum;
import ru.practicum.server.models.AdminFilterParam;
import ru.practicum.server.service.interfaces.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    ResponseEntity<?> getEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                @RequestParam(name = "states", required = false) List<String> states,
                                @RequestParam(name = "categories", required = false) List<Long> categories,
                                @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        AdminFilterParam adminFilterParam = AdminFilterParam.builder()
                .users(users)
                .states(states.stream().map(StateEnum::valueOf).collect(Collectors.toList()))
                .categories(categories)
                .rangeStart(LocalDateTime.parse(rangeStart, formatter))
                .rangeEnd(LocalDateTime.parse(rangeEnd, formatter))
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
