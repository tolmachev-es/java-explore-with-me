package ru.practicum.server.controllers.adminControllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.UpdateEventAdminRequestDto;
import ru.practicum.server.enums.RequestStatusEnum;
import ru.practicum.server.models.FilterParam;
import ru.practicum.server.service.interfaces.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Validated
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
        FilterParam filterParam = FilterParam.builder()
                .users(users)
                .states(states.stream().map(RequestStatusEnum::valueOf).collect(Collectors.toList()))
                .categories(categories)
                .pageable(PageRequest.of(from / size, size))
                .build();
        return eventService.getEvents(filterParam);
    }

    @PatchMapping("/{eventId}")
    ResponseEntity<?> updateEvent(@Valid @RequestBody UpdateEventAdminRequestDto requestDto,
                                  @PathVariable(name = "eventId") Long eventId) {
        return eventService.updateEventByAdmin(eventId, requestDto);
    }
}
