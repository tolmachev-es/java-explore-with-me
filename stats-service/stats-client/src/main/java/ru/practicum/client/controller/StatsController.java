package ru.practicum.client.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.client.StatsSenderClient;
import ru.practicum.client.models.HitDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsSenderClient statsClient;

    @PostMapping("/hit")
    ResponseEntity<?> addStatistic(@Valid @RequestBody HitDto hitDto) {
        return statsClient.addNewHit(hitDto);
    }

    @GetMapping("/stats")
    ResponseEntity<?> getStats(@RequestParam @NotNull String start,
                               @RequestParam @NotNull String end,
                               @RequestParam(required = false) List<String> uris,
                               @RequestParam(defaultValue = "false") boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return statsClient.getStats(LocalDateTime.parse(start, formatter),
                LocalDateTime.parse(end, formatter),
                uris,
                unique);
    }
}