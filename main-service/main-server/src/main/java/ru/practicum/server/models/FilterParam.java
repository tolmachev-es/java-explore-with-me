package ru.practicum.server.models;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import ru.practicum.server.enums.RequestStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
public class FilterParam {
    private List<Long> users;
    private List<RequestStatusEnum> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Pageable pageable;
}
