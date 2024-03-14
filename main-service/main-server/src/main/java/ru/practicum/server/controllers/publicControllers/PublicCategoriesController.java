package ru.practicum.server.controllers.publicControllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.service.interfaces.EventService;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoriesController {
    private final EventService eventService;

    @GetMapping
    ResponseEntity<?> getCategories(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from /size, size);
        return eventService.getPageableCategory(pageable);
    }

    @GetMapping("/{catId}")
    ResponseEntity<?> getCategoryById(@PathVariable(name = "catId") Long catId) {
        return eventService.getCategoryById(catId);
    }
}
