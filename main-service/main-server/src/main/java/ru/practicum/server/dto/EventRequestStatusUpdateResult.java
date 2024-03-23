package ru.practicum.server.dto;

import java.util.List;

public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequest;
    private List<ParticipationRequestDto> rejectedRequest;
}
