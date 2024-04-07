package ru.practicum.server.models;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class CuratorFilterParam {
    private List<Long> curatorId;
    private Integer from;
    private Integer size;
    private Boolean isActual;
    private SortMethod sort;

    public enum SortMethod {VIEWS, EVENT_DATE}

    ;
}
