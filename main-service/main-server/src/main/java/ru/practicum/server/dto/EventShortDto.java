package ru.practicum.server.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Integer views;
}
