package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.repository.entities.RequestEntity;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    Long countAllByEventId_Id(Long eventId);

    List<RequestEntity> getAllByUserId_Id(Long userId);

    Optional<RequestEntity> findByIdAndUserId_Id(Long id, Long userId);

    List<RequestEntity> getAllByEventId_Id(Long eventId);
}
