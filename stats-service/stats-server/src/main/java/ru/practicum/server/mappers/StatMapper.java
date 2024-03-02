package ru.practicum.server.mappers;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.client.models.HitDto;
import ru.practicum.client.models.ViewStatsDto;
import ru.practicum.server.models.ViewStats;
import ru.practicum.server.repository.HitEntity;
import ru.practicum.server.repository.ViewStatsProjections;

import java.util.HashMap;
import java.util.Map;


@Mapper(imports = {HashMap.class, Map.class})
public interface StatMapper {
    StatMapper STAT_MAPPER = Mappers.getMapper(StatMapper.class);

    HitEntity hitToEntity(HitDto hitDto);

    @Mapping(target = "hits", expression = "java(unique ? viewStats.getHits().keySet().size() : " +
            "viewStats.getHits().values().stream().reduce(0, Integer::sum))")
    ViewStatsDto viewToDto(ViewStats viewStats, @Context boolean unique);

    @Mapping(target = "app", expression = "java(projections.getApp())")
    @Mapping(target = "uri", expression = "java(projections.getUri())")
    @Mapping(target = "hits", expression = "java(new HashMap<>(Map.of(projections.getIp(), projections.getCount())))")
    ViewStats fromProjections(ViewStatsProjections projections);
}
