package ru.practicum.server.service.interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.server.dto.categoryDtos.CategoryDto;
import ru.practicum.server.dto.categoryDtos.NewCategoryDto;
import ru.practicum.server.dto.compilationDtos.NewCompilationDto;
import ru.practicum.server.dto.compilationDtos.UpdateCompilationRequest;
import ru.practicum.server.dto.eventDtos.NewEventDto;
import ru.practicum.server.dto.eventDtos.UpdateEventAdminRequestDto;
import ru.practicum.server.dto.requestDtos.EventRequestStatusUpdateDto;
import ru.practicum.server.dto.requestDtos.UpdateEventUserRequestDto;
import ru.practicum.server.models.AdminFilterParam;
import ru.practicum.server.models.PublicFilterParam;

@Service
public interface EventService {
    ResponseEntity<?> getByUserId(Long userId, Pageable pageable);

    ResponseEntity<?> createNewEvent(NewEventDto newEventDto, Long userid);

    ResponseEntity<?> getByEventId(Long userId, Long eventId);

    ResponseEntity<?> updateEvent(UpdateEventUserRequestDto updateEvent, Long userId, Long eventId);

    ResponseEntity<?> getRequestByEvent(Long userId, Long eventId);

    ResponseEntity<?> changeStatusForEvent(EventRequestStatusUpdateDto requestIntegerStatusUpdateDto, Long userId, Long eventId);

    ResponseEntity<?> createCategory(NewCategoryDto newCategoryDto);

    ResponseEntity<?> removeCategory(Long categoryId);

    ResponseEntity<?> updateCategory(CategoryDto categoryDto, Long categoryId);

    ResponseEntity<?> getPageableCategory(Pageable pageable);

    ResponseEntity<?> getCategoryById(Long categoryId);

    ResponseEntity<?> createRequest(Long userId, Long eventId);

    ResponseEntity<?> getRequestByUser(Long userId);

    ResponseEntity<?> removeRequest(Long requestId, Long userId);

    ResponseEntity<?> getEvents(AdminFilterParam adminFilterParam);

    ResponseEntity<?> updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto requestDto);

    ResponseEntity<?> createCompilation(NewCompilationDto newCompilationDto);

    ResponseEntity<?> getCompilationById(Long compId);

    ResponseEntity<?> getPageableCompilation(Boolean pinned, Pageable pageable);

    ResponseEntity<?> removeCompilation(Long id);

    ResponseEntity<?> updateCompilation(Long compilationId, UpdateCompilationRequest request);

    ResponseEntity<?> getEventsByPublic(PublicFilterParam filterParam);

    ResponseEntity<?> getEventByIdPublic(Long eventId);
}
