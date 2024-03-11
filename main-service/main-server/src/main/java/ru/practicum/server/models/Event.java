package ru.practicum.server.models;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import ru.practicum.server.enums.StateEnum;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class Event {
    private Long id;
    private String annotation;
    private Category category;
    private String description;
    private LocalDateTime eventDate;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private User owner;
    private StateEnum state;
    private Integer views;
}
