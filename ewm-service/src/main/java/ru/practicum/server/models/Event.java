package ru.practicum.server.models;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import ru.practicum.server.dto.eventDtos.LocationDto;
import ru.practicum.server.enums.RequestStatusEnum;
import ru.practicum.server.enums.StateEnum;
import ru.practicum.server.repository.entities.RequestEntity;

import java.time.LocalDateTime;
import java.util.List;

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
    private LocationDto location;
    private Boolean requestModeration;
    private String title;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private User owner;
    private StateEnum state;
    private List<RequestEntity> requestEntities;
    private Integer views;

    public long getCountConfirmedRequests() {
        if (requestEntities == null) {
            return 0;
        } else {
            return requestEntities.stream()
                    .filter(e -> e.getConfirmed().equals(RequestStatusEnum.CONFIRMED))
                    .count();
        }
    }
}
