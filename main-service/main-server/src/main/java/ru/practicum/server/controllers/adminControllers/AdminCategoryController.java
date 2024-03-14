package ru.practicum.server.controllers.adminControllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.CategoryDto;
import ru.practicum.server.dto.NewCategoryDto;
import ru.practicum.server.service.interfaces.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {
    private final EventService eventService;

    @PostMapping()
    ResponseEntity<?> createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return eventService.createCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    ResponseEntity<?> removeCategory(@PathVariable(name = "catId") Long catId) {
        return eventService.removeCategory(catId);
    }

    @PatchMapping("/{catId}")
    ResponseEntity<?> updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                     @PathVariable(name = "catId") Long catId) {
        return eventService.updateCategory(categoryDto, catId);
    }
}
