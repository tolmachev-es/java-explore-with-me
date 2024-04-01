package ru.practicum.server.exceptions;

public class StatisticBadRequestException extends RuntimeException {
    public StatisticBadRequestException(String message) {
        super(message);
    }
}

