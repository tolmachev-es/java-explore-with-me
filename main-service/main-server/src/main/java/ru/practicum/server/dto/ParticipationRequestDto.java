package ru.practicum.server.dto;

import lombok.Data;
import ru.practicum.server.enums.StateEnum;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {
    private LocalDateTime created;
    private Long eventId;
    private Long id;
    private Long requester;
    private StateEnum state;
}
