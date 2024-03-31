package ru.practicum.server.service.interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.server.dto.categoryDtos.CategoryDto;
import ru.practicum.server.dto.categoryDtos.NewCategoryDto;
import ru.practicum.server.dto.compilationDtos.CompilationDto;
import ru.practicum.server.dto.compilationDtos.NewCompilationDto;
import ru.practicum.server.dto.compilationDtos.UpdateCompilationRequest;
import ru.practicum.server.dto.eventDtos.EventFullDto;
import ru.practicum.server.dto.eventDtos.EventShortDto;
import ru.practicum.server.dto.eventDtos.NewEventDto;
import ru.practicum.server.dto.eventDtos.UpdateEventAdminRequestDto;
import ru.practicum.server.dto.requestDtos.EventRequestStatusUpdateDto;
import ru.practicum.server.dto.requestDtos.EventRequestStatusUpdateResult;
import ru.practicum.server.dto.requestDtos.ParticipationRequestDto;
import ru.practicum.server.dto.requestDtos.UpdateEventUserRequestDto;
import ru.practicum.server.models.AdminFilterParam;
import ru.practicum.server.models.PublicFilterParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public interface EventService {
    List<EventShortDto> getByUserId(Long userId, Pageable pageable);

    EventFullDto createNewEvent(NewEventDto newEventDto, Long userid);

    EventFullDto getByEventId(Long userId, Long eventId);

    EventFullDto updateEvent(UpdateEventUserRequestDto updateEvent, Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestByEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeStatusForEventRequests(EventRequestStatusUpdateDto requestIntegerStatusUpdateDto, Long userId, Long eventId);

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void removeCategory(Long categoryId);

    CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId);

    List<CategoryDto> getPageableCategory(Pageable pageable);

    CategoryDto getCategoryById(Long categoryId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestByUser(Long userId);

    ParticipationRequestDto removeRequest(Long requestId, Long userId);

    List<EventFullDto> getEvents(AdminFilterParam adminFilterParam);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto requestDto);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto getCompilationById(Long compId);

    List<CompilationDto> getPageableCompilation(Boolean pinned, Pageable pageable);

    void removeCompilation(Long id);

    CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest request);

    List<EventShortDto> getEventsByPublic(PublicFilterParam filterParam, HttpServletRequest httpServletRequest);

    EventFullDto getEventByIdPublic(Long eventId, HttpServletRequest httpServletRequest);
}
