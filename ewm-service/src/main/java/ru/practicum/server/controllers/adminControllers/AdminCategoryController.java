package ru.practicum.server.controllers.adminControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.categoryDtos.CategoryDto;
import ru.practicum.server.dto.categoryDtos.NewCategoryDto;
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
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Has new request create category with name {}", newCategoryDto.getName());
        return new ResponseEntity<>(eventService.createCategory(newCategoryDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Object> removeCategory(@PathVariable(name = "catId") Long catId) {
        log.info("Has new request to remove category with id {}", catId);
        eventService.removeCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                                      @PathVariable(name = "catId") Long catId) {
        log.info("Has new request to update category with id {}", catId);
        return new ResponseEntity<>(eventService.updateCategory(categoryDto, catId), HttpStatus.OK);
    }
}
