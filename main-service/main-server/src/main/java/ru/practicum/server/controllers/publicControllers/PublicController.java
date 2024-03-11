package ru.practicum.server.controllers.publicControllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.service.interfaces.EventService;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PublicController {
    private final EventService eventService;

    @GetMapping("/compilations")
    ResponseEntity<?> getCompilations(@RequestParam(name = "pinned") Boolean pinned,
                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return null;
    }

    @GetMapping("/compilations/{compId}")
    ResponseEntity<?> getCompilationById(@PathVariable(name = "compId") Long compId) {
        return null;

    }

    @GetMapping("/categories")
    ResponseEntity<?> getCategories(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return null;
    }

    @GetMapping("/categories/{catId}")
    ResponseEntity<?> getCategoryById(@PathVariable(name = "catId") Long catId) {
        return eventService.getCategoryById(catId);
    }
}
