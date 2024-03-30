package ru.practicum.server.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.client.models.HitDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    ResponseEntity<?> addStatistic(HitDto hitDto);

    ResponseEntity<?> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
