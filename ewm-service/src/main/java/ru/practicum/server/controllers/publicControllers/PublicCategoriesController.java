package ru.practicum.server.controllers.publicControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.categoryDtos.CategoryDto;
import ru.practicum.server.service.interfaces.EventService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class PublicCategoriesController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                                           @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Has new request to get categories");
        return new ResponseEntity<>(eventService.getPageableCategory(pageable), HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable(name = "catId") Long catId) {
        log.info("Has new request to get category with id {}", catId);
        return new ResponseEntity<>(eventService.getCategoryById(catId), HttpStatus.OK);
    }
}
