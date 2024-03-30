package ru.practicum.server.exceptions;

public class IncorrectSearchDate extends RuntimeException {
    public IncorrectSearchDate(String message) {
        super(message);
    }
}
