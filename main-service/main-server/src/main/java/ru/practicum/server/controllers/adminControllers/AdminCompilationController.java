package ru.practicum.server.controllers.adminControllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.NewCompilationDto;
import ru.practicum.server.dto.UpdateCompilationRequest;
import ru.practicum.server.service.interfaces.EventService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Validated
public class AdminCompilationController {
    private final EventService eventService;


    @PostMapping("/compilations")
    ResponseEntity<?> createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return eventService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    ResponseEntity<?> removeCompilation(@PositiveOrZero @PathVariable(name = "compId") Long compId) {
        return eventService.removeCompilation(compId);
    }

    @PatchMapping("/compilation/{compId}")
    ResponseEntity<?> updateCompilation(@Valid @RequestBody UpdateCompilationRequest updateCompilationRequest,
                                        @PositiveOrZero @PathVariable(name = "compId") Long compId) {
        return eventService.updateCompilation(compId, updateCompilationRequest);
    }


}
