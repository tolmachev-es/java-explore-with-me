package ru.practicum.client.client;

import org.springframework.stereotype.Service;
import ru.practicum.client.models.ViewStatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
@Service
public interface StatsClient {

    void addNewHit(String app, HttpServletRequest request);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
