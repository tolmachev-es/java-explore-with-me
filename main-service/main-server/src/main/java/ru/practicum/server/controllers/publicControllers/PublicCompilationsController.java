package ru.practicum.server.controllers.publicControllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.service.interfaces.EventService;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationsController {
    private final EventService eventService;

    @GetMapping
    ResponseEntity<?> getCompilations(@Nullable @RequestParam(name = "pinned") Boolean pinned,
                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventService.getPageableCompilation(pinned, pageable);
    }

    @GetMapping("/{compId}")
    ResponseEntity<?> getCompilationById(@PathVariable(name = "compId") Long compId) {
        return eventService.getCompilationById(compId);
    }
}
