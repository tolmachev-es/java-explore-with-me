package ru.practicum.server.dto.eventDtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.server.enums.AdminStateActionEnum;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequestDto {
    @Length(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Length(min = 20, max = 7000)
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    private LocalDateTime eventDate;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private AdminStateActionEnum stateAction;
    @Length(min = 3, max = 120)
    private String title;
}
