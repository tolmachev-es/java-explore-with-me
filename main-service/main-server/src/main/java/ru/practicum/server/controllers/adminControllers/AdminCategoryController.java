package ru.practicum.server.controllers.adminControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.CategoryDto;
import ru.practicum.server.dto.NewCategoryDto;
import ru.practicum.server.service.interfaces.EventService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCategoryController {
    private final EventService eventService;

    @PostMapping
    ResponseEntity<?> createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Has new request create category with name {}", newCategoryDto.getName());
        return eventService.createCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    ResponseEntity<?> removeCategory(@PathVariable(name = "catId") Long catId) {
        log.info("Has new request to remove category with id {}", catId);
        return eventService.removeCategory(catId);
    }

    @PatchMapping("/{catId}")
    ResponseEntity<?> updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                     @PathVariable(name = "catId") Long catId) {
        log.info("Has new request to update category with id {}", catId);
        return eventService.updateCategory(categoryDto, catId);
    }
}
