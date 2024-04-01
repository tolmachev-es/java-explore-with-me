package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.enums.RequestStatusEnum;
import ru.practicum.server.repository.entities.RequestEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    Long countAllByEventId_Id(Long eventId);

    List<RequestEntity> getAllByUserId_Id(Long userId);

    Optional<RequestEntity> findByIdAndUserId_Id(Long id, Long userId);

    List<RequestEntity> getAllByEventId_Id(Long eventId);

    List<RequestEntity> getAllByEventId_IdIn(Collection<Long> eventId_id);

    Long countAllByEventId_IdAndConfirmed(Long eventIdId, RequestStatusEnum confirmed);

    List<RequestEntity> getAllByIdInAndConfirmed(Collection<Long> id, RequestStatusEnum confirmed);

    List<RequestEntity> getAllByEventId_idAndConfirmed(Long eventId, RequestStatusEnum confirmed);

    List<RequestEntity> getAllByIdIn(Collection<Long> id);

    Integer countByEventId_IdAndConfirmed(Long eventId, RequestStatusEnum confirmed);
}
