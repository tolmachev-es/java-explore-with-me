package ru.practicum.server.controllers.adminControllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.dto.NewCompilationDto;
import ru.practicum.server.dto.UpdateCompilationRequest;
import ru.practicum.server.service.interfaces.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Validated
public class AdminController {
    private final EventService eventService;


    @PostMapping("/compilations")
    ResponseEntity<?> createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto,
                                        HttpServletRequest request) {
        return null;
    }

    @DeleteMapping("/compilations/{compId}")
    ResponseEntity<?> removeCompilation(@PositiveOrZero @PathVariable(name = "compId") Long compId,
                                        HttpServletRequest request) {
        return null;
    }

    @PatchMapping("/compilation/{compId}")
    ResponseEntity<?> updateCompilation(@Valid @RequestBody UpdateCompilationRequest updateCompilationRequest,
                                        @PositiveOrZero @PathVariable(name = "compId") Long compId,
                                        HttpServletRequest request) {
        return null;
    }

    @GetMapping("/events")
    ResponseEntity<?> getEvents(@RequestParam(name = "users") List<Long> users,
                                @RequestParam(name = "states") List<String> states,
                                @RequestParam(name = "categories") List<Long> categories,
                                @RequestParam(name = "rangeStart") LocalDateTime rangeStart,
                                @RequestParam(name = "rangeEnd") LocalDateTime rangeEnd,
                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return null;
    }
}
