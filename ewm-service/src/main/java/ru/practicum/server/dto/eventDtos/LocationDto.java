package ru.practicum.server.dto.eventDtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationDto {
    private Float lat;
    private Float lon;
}
