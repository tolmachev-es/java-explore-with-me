package ru.practicum.server.exceptions;

import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.server.dto.ApiError;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class MainErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, ValidationException.class})
    public ResponseEntity<?> handle(final Exception ex) {
        Map<String, String> error = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError fe : errors) {
                error.put(fe.getField(), fe.getDefaultMessage());
            }
        }
        ApiError apiError = ApiError.builder()
                .errors(error.entrySet().stream().map(e -> e.getKey() + " " + e.getValue()).collect(Collectors.toList()))
                .status(String.valueOf(HttpStatus.BAD_REQUEST))
                .reason("Incorrectly made request.")
                .message("add")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AlreadyUseException.class})
    public ResponseEntity<?> handle(final AlreadyUseException already) {
        ApiError apiError = ApiError.builder()
                .status(String.valueOf(HttpStatus.CONFLICT))
                .reason("Integrity constraint has been violated.")
                .message(already.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<?> handle(final NotFoundException notFoundException) {
        ApiError apiError = ApiError.builder()
                .status(String.valueOf(HttpStatus.NOT_FOUND))
                .reason("The required object was not found")
                .message(notFoundException.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IncorrectDateException.class})
    public ResponseEntity<?> handle(final IncorrectDateException incorrectDateException) {
        ApiError apiError = ApiError.builder()
                .status(String.valueOf(HttpStatus.NOT_FOUND))
                .reason("Incorrect date exception")
                .message(incorrectDateException.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({PSQLException.class})
    public ResponseEntity<?> handle(final PSQLException psqlException) {
        ApiError apiError = ApiError.builder()
                .status(String.valueOf(HttpStatus.CONFLICT))
                .reason("Duplicate value in database")
                .message(psqlException.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({IncorrectRequestException.class})
    public ResponseEntity<?> handle(final IncorrectRequestException incorrectRequestException) {
        ApiError apiError = ApiError.builder()
                .status(String.valueOf(HttpStatus.CONFLICT))
                .reason("Incorrect request exception")
                .message(incorrectRequestException.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
}
