package ru.practicum.server.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.models.HitDto;
import ru.practicum.server.service.StatsService;

import javax.validation.constraints.NotNull;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    ResponseEntity<?> addStatistic(@RequestBody HitDto hitDto) {
        return service.addStatistic(hitDto);
    }

    @GetMapping("/stats")
    ResponseEntity<?> getStats(@RequestParam @NotNull String start,
                               @RequestParam @NotNull String end,
                               @RequestParam(required = false) List<String> uris,
                               @RequestParam(defaultValue = "false") String unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        boolean unic = Boolean.parseBoolean(unique);
        return service.getStats(LocalDateTime.parse(URLDecoder.decode(start, Charset.defaultCharset()), formatter),
                LocalDateTime.parse((URLDecoder.decode(end, Charset.defaultCharset())), formatter),
                uris,
                unic);
    }
}
