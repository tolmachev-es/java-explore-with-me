package ru.practicum.server.dto;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequest;
    private List<ParticipationRequestDto> rejectedRequest;
}
