package ru.practicum.server.service;

import ru.practicum.client.models.HitDto;
import ru.practicum.client.models.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void addStatistic(HitDto hitDto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
