package ru.practicum.server.dto;

import lombok.Data;
import ru.practicum.server.enums.EventRequestStatusEnum;

@Data
public class EventRequestStatusUpdateDto {
    private int[] requestIds;
    private EventRequestStatusEnum status;
}
