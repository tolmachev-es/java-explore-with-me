package ru.practicum.server.controllers.publicControllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.server.models.PublicFilterParam;
import ru.practicum.server.service.interfaces.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventsController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<?> getEvents(@RequestParam(name = "text") String text,
                                       @RequestParam(name = "categories")List<Long> categories,
                                       @RequestParam(name = "paid") Boolean paid,
                                       @RequestParam(name = "rangeStart") LocalDateTime rangeStart,
                                       @RequestParam(name = "rangeEnd") LocalDateTime rangeEnd,
                                       @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                       @RequestParam(name = "sort") PublicFilterParam.SortMethod sort,
                                       @RequestParam(name = "from", defaultValue = "0") Integer from,
                                       @RequestParam(name = "size", defaultValue = "10") Integer size) {
        PublicFilterParam filterParam = PublicFilterParam.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .start(rangeStart)
                .end(rangeEnd)
                .available(onlyAvailable)
                .sort(sort)
                .pageable(PageRequest.of(from / size, size))
                .build();
        return eventService.getEventsByPublic(filterParam);
    }
}
