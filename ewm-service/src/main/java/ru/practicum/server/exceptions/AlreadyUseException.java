package ru.practicum.server.exceptions;

public class AlreadyUseException extends RuntimeException {
    public AlreadyUseException(String message) {
        super(message);
    }
}
