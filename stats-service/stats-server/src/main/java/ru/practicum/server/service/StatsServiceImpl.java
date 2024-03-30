package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.models.HitDto;
import ru.practicum.client.models.ViewStatsDto;
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
    public ResponseEntity<?> addStatistic(HitDto hitDto) {
        HitEntity hitEntity = StatMapper.STAT_MAPPER.hitToEntity(hitDto);
        try {
            statRepository.save(hitEntity);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (end.isBefore(start)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            List<ViewStatsDto> viewStatsDtos = statRepository.findAllStatistic(start, end, uris)
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
            return new ResponseEntity<>(viewStatsDtos, HttpStatus.OK);
        }
    }
}
