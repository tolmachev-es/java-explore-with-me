package ru.practicum.server.controllers.publicControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.models.PublicFilterParam;
import ru.practicum.server.service.interfaces.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventsController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<?> getEvents(@RequestParam(name = "text", required = false) String text,
                                       @RequestParam(name = "categories", required = false) List<Long> categories,
                                       @RequestParam(name = "paid", required = false) Boolean paid,
                                       @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                       @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                       @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                       @RequestParam(name = "sort", required = false) PublicFilterParam.SortMethod sort,
                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        PublicFilterParam filterParam = PublicFilterParam.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .start(rangeStart == null ? null : LocalDateTime.parse(rangeStart, formatter))
                .end(rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, formatter))
                .available(onlyAvailable)
                .sort(sort)
                .pageable(PageRequest.of(from / size, size))
                .build();
        log.info("Has new request to get events with filter params {}", filterParam.toString());
        return eventService.getEventsByPublic(filterParam);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable(name = "eventId") Long eventId) {
        log.info("Has new request to get event with id {}", eventId);
        return eventService.getEventByIdPublic(eventId);
    }
}
