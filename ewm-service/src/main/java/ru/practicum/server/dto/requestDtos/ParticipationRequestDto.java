package ru.practicum.server.dto.requestDtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.server.enums.RequestStatusEnum;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {
    private Long id;
    private Long requester;
    @JsonProperty("event")
    private Long event;
    @JsonProperty("status")
    private RequestStatusEnum state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime created;
}
