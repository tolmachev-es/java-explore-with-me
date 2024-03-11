package ru.practicum.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.repository.entities.RequestEntity;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class RequestDB {
    private final RequestRepository repository;

    public List<RequestEntity> getByUserId(Long userId) {
        return repository.getAllByUserId_Id(userId);
    }


    public RequestEntity createNewRequest(RequestEntity request) {
        repository.save(request);
        return request;
    }

    public Long countGuest(Long eventId) {
        return repository.countAllByEventId_Id(eventId);
    }

    public void removeRequest(Long requestId, Long userId) {
        Optional<RequestEntity> request = repository.findByIdAndUserId_Id(requestId, userId);
        if (request.isPresent()) {
            repository.delete(request.get());
        } else {
            throw new NotFoundException("Не найден реквест");
        }
    }

    public List<RequestEntity> getRequestsByEvent(Long eventId) {
        return repository.getAllByEventId_Id(eventId);
    }
}
