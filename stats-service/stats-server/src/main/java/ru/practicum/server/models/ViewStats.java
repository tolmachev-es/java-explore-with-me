package ru.practicum.server.models;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Objects;

@Data
@SuperBuilder
public class ViewStats {
    private String app;
    private String uri;
    private Map<String, Integer> hits;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewStats viewStats = (ViewStats) o;
        return Objects.equals(app, viewStats.app) && Objects.equals(uri, viewStats.uri); }

    @Override
    public int hashCode() {
        return Objects.hash(app, uri);
    }
}
