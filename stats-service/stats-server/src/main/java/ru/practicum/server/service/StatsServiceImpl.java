package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.client.models.HitDto;
import ru.practicum.client.models.ViewStatsDto;
import ru.practicum.server.mappers.StatMapper;
import ru.practicum.server.models.ViewStats;
import ru.practicum.server.repository.HitEntity;
import ru.practicum.server.repository.StatRepository;

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
        return statRepository.findAllStatistic(start, end, uris)
                .stream()
                .map(StatMapper.STAT_MAPPER::fromProjections)
                .map(v -> StatMapper.STAT_MAPPER.viewToDto(v, unique))
                .sorted(new Comparator<ViewStatsDto>() {
                    @Override
                    public int compare(ViewStatsDto o1, ViewStatsDto o2) {
                        return o2.getHits() - o1.getHits();
                    }
                })
                .collect(Collectors.toList());

    }


    private List<ViewStats> toViewStatsList(List<Object[]> objects) {
        Map<String, ViewStats> allStats = new HashMap<>();
        for (Object[] object : objects) {
            String key = object[0].toString() + object[1].toString();
            if (allStats.containsKey(key)) {
                allStats.get(key).getHits().put(object[2].toString(), Integer.parseInt(object[3].toString()));
            } else {
                Map<String, Integer> hits = new HashMap<>() {{
                    put("hi", 1);
                }};
                hits.put(object[2].toString(), Integer.parseInt(object[3].toString()));
            }
        }
        return new ArrayList<>(allStats.values());
    }

    private List<ViewStatsDto> toViewStatsDtoList(List<Object[]> objects, boolean unique) {
        List<ViewStats> viewStats = toViewStatsList(objects);
        return viewStats.stream()
                .map(view -> StatMapper.STAT_MAPPER.viewToDto(view, unique))
                .sorted(new Comparator<ViewStatsDto>() {
                    @Override
                    public int compare(ViewStatsDto o1, ViewStatsDto o2) {
                        return o2.getHits() - o1.getHits();
                    }
                })
                .collect(Collectors.toList());
    }
}
