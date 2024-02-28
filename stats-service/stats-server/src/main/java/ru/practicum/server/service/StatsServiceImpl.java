package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.server.mappers.StatMapper;
import ru.practicum.server.models.ViewStats;
import ru.practicum.server.repository.HitEntity;
import ru.practicum.server.repository.StatRepository;
import ru.practicum.client.models.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatRepository statRepository;
    @Override
    public void addStatistic(HitDto hitDto) {
        HitEntity hitEntity = StatMapper.STAT_MAPPER.hitToEntity(hitDto);
        statRepository.save(hitEntity);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return toViewStatsDtoList(statRepository.findAllStatistic(start, end, uris), unique);
    }


    private List<ViewStats> toViewStatsList(List<Object[]> objects) {
        Map<String, ViewStats> allStats = new HashMap<>();
        for (Object[] object: objects) {
            String key = object[0].toString() + object[1].toString();
            if (allStats.containsKey(key)) {
                allStats.get(key).getHits().put(object[2].toString(), Integer.parseInt(object[3].toString()));
            } else {
                Map<String, Integer> hits = new HashMap<>();
                hits.put(object[2].toString(), Integer.parseInt(object[3].toString()));
                ViewStats newViewStats = ViewStats.builder()
                        .app(object[0].toString())
                        .uri(object[1].toString())
                        .hits(hits)
                        .build();
                allStats.put(key, newViewStats);
            }
        }
        return new ArrayList<>(allStats.values());
    }

    private List<ViewStatsDto> toViewStatsDtoList(List<Object[]> objects, boolean unique) {
        List<ViewStats> viewStats = toViewStatsList(objects);
        return viewStats.stream()
                .map(view -> StatMapper.STAT_MAPPER.viewToDto(view, unique))
                .collect(Collectors.toList());
    }
}
