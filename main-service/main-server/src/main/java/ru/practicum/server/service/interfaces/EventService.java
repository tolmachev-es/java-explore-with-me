package ru.practicum.server.service.interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.server.dto.*;

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
}
