package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.client.models.HitDto;
import ru.practicum.client.models.ViewStatsDto;
import ru.practicum.server.exceptions.StatisticBadRequestException;
import ru.practicum.server.mappers.StatMapper;
import ru.practicum.server.repository.HitEntity;
import ru.practicum.server.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatRepository statRepository;

    @Override
    public void addStatistic(HitDto hitDto) {
        HitEntity hitEntity = StatMapper.STAT_MAPPER.hitToEntity(hitDto);
        try {
            statRepository.save(hitEntity);
        } catch (Exception e) {
            throw new StatisticBadRequestException("Произошла ошибка при добавлении статистики");
        }
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (end.isBefore(start)) {
            throw new StatisticBadRequestException("Дата начала позже даты окончания");
        } else {
            return statRepository.findAllStatistic(start, end, uris)
                    .stream()
                    .map(StatMapper.STAT_MAPPER::fromProjections)
                    .map(v -> StatMapper.STAT_MAPPER.viewToDto(v, unique))
                    .sorted(Comparator.comparing(ViewStatsDto::getHits))
                    .collect(Collectors.toList());
        }
    }
}
