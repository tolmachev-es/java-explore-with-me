package ru.practicum.client.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ViewStatsDto {
    private String app;
    private String uri;
    private int hits;
}
