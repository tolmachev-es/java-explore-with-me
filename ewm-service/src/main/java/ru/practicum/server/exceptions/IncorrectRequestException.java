package ru.practicum.server.exceptions;

public class IncorrectRequestException extends RuntimeException {
    public IncorrectRequestException(String message) {
        super(message);
    }
}
