package ru.practicum.server.dto;

import lombok.Data;
import ru.practicum.server.enums.EventRequestStatusEnum;

@Data
public class EventRequestStatusUpdateDto {
    private Long[] requestIds;
    private EventRequestStatusEnum status;
}
