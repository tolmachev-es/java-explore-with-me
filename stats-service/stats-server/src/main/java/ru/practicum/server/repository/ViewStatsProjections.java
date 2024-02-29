package ru.practicum.server.repository;

public interface ViewStatsProjections {
    String getUri();

    String getApp();

    String getIp();

    Integer getCount();
}
