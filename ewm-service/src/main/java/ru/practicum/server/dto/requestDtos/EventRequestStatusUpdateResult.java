package ru.practicum.server.dto.requestDtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EventRequestStatusUpdateResult {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
}
