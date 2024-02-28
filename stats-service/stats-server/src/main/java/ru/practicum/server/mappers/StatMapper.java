package ru.practicum.server.mappers;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.server.models.ViewStats;
import ru.practicum.server.repository.HitEntity;
import ru.practicum.client.models.HitDto;
import ru.practicum.client.models.ViewStatsDto;

@Mapper
public interface StatMapper {
    StatMapper STAT_MAPPER = Mappers.getMapper(StatMapper.class);

    HitEntity hitToEntity(HitDto hitDto);

    @Mapping(target = "hits", expression = "java(unique ? viewStats.getHits().keySet().size() : " +
            "viewStats.getHits().values().stream().reduce(0, Integer::sum))")
    ViewStatsDto viewToDto(ViewStats viewStats, @Context boolean unique);
}
