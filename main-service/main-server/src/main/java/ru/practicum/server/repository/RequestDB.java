package ru.practicum.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.enums.EventRequestStatusEnum;
import ru.practicum.server.enums.RequestStatusEnum;
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

    public List<RequestEntity> getByIds(Long[] requestIds) {
        return repository.getAllByIdIn(List.of(requestIds));
    }


    @Transactional
    public List<RequestEntity> requestConfirmStatus(Long[] requestIds, int limit, Long eventId) {
        if (limit > 0) {
            Long countConfirmed = repository.countAllByEventId_IdAndConfirmed(eventId, RequestStatusEnum.PENDING);
            if (countConfirmed + requestIds.length > limit) {
                throw new RuntimeException("Количество заявок превышает количество возможных");
            } else if (countConfirmed + requestIds.length == limit) {
                return confirmRequests(requestIds);
            } else {
                List<RequestEntity> requestEntities = confirmRequests(requestIds);
                changeStatusForEvent(eventId, EventRequestStatusEnum.REJECTED);
                return requestEntities;
            }
        } else {
            return confirmRequests(requestIds);
        }
    }

    public List<RequestEntity> requestRejectStatus(Long[] requestIds) {
        List<RequestEntity> requestEntities = repository.getAllByIdInAndConfirmed(
                List.of(requestIds), RequestStatusEnum.PENDING);
        if (requestEntities.size() < requestIds.length) {
            throw new RuntimeException("Нет заявок в нужном статусе");
        } else {
            for (RequestEntity request : requestEntities) {
                request.setConfirmed(RequestStatusEnum.REJECTED);
                repository.save(request);
            }
        }
        return requestEntities;
    }

    private List<RequestEntity> confirmRequests(Long[] requestsIds) {
        List<RequestEntity> requestEntities = repository.getAllByIdInAndConfirmed(
                List.of(requestsIds), RequestStatusEnum.PENDING);
        if (requestEntities.size() < requestsIds.length) {
            throw new RuntimeException("Нет заявок в нужном статусе");
        } else {
            for (RequestEntity request : requestEntities) {
                request.setConfirmed(RequestStatusEnum.ALLOWS);
                repository.save(request);
            }
        }
        return requestEntities;
    }

    private void changeStatusForEvent(Long eventId, EventRequestStatusEnum statusEnum) {
        List<RequestEntity> requestEntities = repository.getAllByEventId_idAndConfirmed(
                eventId, RequestStatusEnum.PENDING);
        RequestStatusEnum requestStatusEnum;
        switch (statusEnum) {
            case REJECTED:
                requestStatusEnum = RequestStatusEnum.REJECTED;
                break;
            case CONFIRMED:
                requestStatusEnum = RequestStatusEnum.ALLOWS;
                break;
            default:
                throw new RuntimeException("Unsupported command");
        }
        for (RequestEntity request : requestEntities) {
            request.setConfirmed(requestStatusEnum);
            repository.save(request);
        }
    }
}
