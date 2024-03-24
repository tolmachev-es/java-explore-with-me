package ru.practicum.server.controllers.adminControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.NewCompilationDto;
import ru.practicum.server.dto.UpdateCompilationRequest;
import ru.practicum.server.service.interfaces.EventService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCompilationController {
    private final EventService eventService;


    @PostMapping
    ResponseEntity<?> createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Has new request to create compilation with name {}", newCompilationDto.getTitle());
        return eventService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    ResponseEntity<?> removeCompilation(@PositiveOrZero @PathVariable(name = "compId") Long compId) {
        log.info("Has new request to remove compilation with id {}", compId);
        return eventService.removeCompilation(compId);
    }

    @PatchMapping("/{compId}")
    ResponseEntity<?> updateCompilation(@Valid @RequestBody UpdateCompilationRequest updateCompilationRequest,
                                        @PositiveOrZero @PathVariable(name = "compId") Long compId) {
        log.info("Has new request to update compilation with id {}", compId);
        return eventService.updateCompilation(compId, updateCompilationRequest);
    }


}
