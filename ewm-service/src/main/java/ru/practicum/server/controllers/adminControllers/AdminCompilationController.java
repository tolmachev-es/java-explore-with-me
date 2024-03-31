package ru.practicum.server.controllers.adminControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.compilationDtos.CompilationDto;
import ru.practicum.server.dto.compilationDtos.NewCompilationDto;
import ru.practicum.server.dto.compilationDtos.UpdateCompilationRequest;
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
    public ResponseEntity<CompilationDto> createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Has new request to create compilation with name {}", newCompilationDto.getTitle());
        return new ResponseEntity<>(eventService.createCompilation(newCompilationDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Object> removeCompilation(@PositiveOrZero @PathVariable(name = "compId") Long compId) {
        log.info("Has new request to remove compilation with id {}", compId);
        eventService.removeCompilation(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@Valid @RequestBody UpdateCompilationRequest updateCompilationRequest,
                                                            @PositiveOrZero @PathVariable(name = "compId") Long compId) {
        log.info("Has new request to update compilation with id {}", compId);
        return new ResponseEntity<>(eventService.updateCompilation(compId, updateCompilationRequest), HttpStatus.OK);
    }


}
