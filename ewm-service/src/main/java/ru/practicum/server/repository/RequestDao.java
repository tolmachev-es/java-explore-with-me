package ru.practicum.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.enums.EventRequestStatusEnum;
import ru.practicum.server.enums.RequestStatusEnum;
import ru.practicum.server.exceptions.IncorrectRequestException;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.repository.entities.RequestEntity;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class RequestDao {
    private final RequestRepository repository;

    @Transactional
    public List<RequestEntity> getByUserId(Long userId) {
        return repository.getAllByUserId_Id(userId);
    }

    @Transactional
    public RequestEntity createNewRequest(RequestEntity request) {
        repository.save(request);
        return request;
    }

    @Transactional
    public RequestEntity removeRequest(Long requestId, Long userId) {
        Optional<RequestEntity> request = repository.findByIdAndUserId_Id(requestId, userId);
        if (request.isPresent()) {
            RequestEntity requestEntity = request.get();
            requestEntity.setConfirmed(RequestStatusEnum.CANCELED);
            repository.save(requestEntity);
            return requestEntity;
        } else {
            throw new NotFoundException("Не найден реквест");
        }
    }

    @Transactional
    public List<RequestEntity> getRequestsByEvent(Long eventId) {
        return repository.getAllByEventId_Id(eventId);
    }

    @Transactional
    public List<RequestEntity> getByIds(Long[] requestIds) {
        return repository.getAllByIdIn(List.of(requestIds));
    }


    @Transactional
    public List<RequestEntity> requestConfirmStatus(Long[] requestIds, int limit, Long eventId) {
        if (limit > 0) {
            Integer countConfirmed = repository.countByEventId_IdAndConfirmed(eventId, RequestStatusEnum.CONFIRMED);
            if (countConfirmed + requestIds.length > limit) {
                throw new IncorrectRequestException("Количество заявок превышает количество возможных");
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

    @Transactional
    public List<RequestEntity> requestRejectStatus(Long[] requestIds) {
        List<RequestEntity> requestEntities = repository.getAllByIdInAndConfirmed(
                List.of(requestIds), RequestStatusEnum.PENDING);
        if (requestEntities.size() < requestIds.length) {
            throw new IncorrectRequestException("Нет заявок в нужном статусе");
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
                request.setConfirmed(RequestStatusEnum.CONFIRMED);
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
                requestStatusEnum = RequestStatusEnum.CONFIRMED;
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
