package ru.practicum.server.dto.eventDtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.server.dto.categoryDtos.CategoryDto;
import ru.practicum.server.dto.userDtos.UserShortDto;
import ru.practicum.server.enums.StateEnum;

import java.time.LocalDateTime;

@Data
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private StateEnum state;
    private String title;
    private Integer views;
}
