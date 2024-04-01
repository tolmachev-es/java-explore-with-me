package ru.practicum.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class StatisticMainExceptionHandler {
    @ExceptionHandler({StatisticBadRequestException.class})
    public ResponseEntity<Object> handle(final StatisticBadRequestException already) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
