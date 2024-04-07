package ru.practicum.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.server.enums.StateEnum;
import ru.practicum.server.repository.entities.EventEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
    Page<EventEntity> getEventEntitiesByOwnerId(Long ownerId, Pageable pageable);

    Optional<EventEntity> getEventEntitiesByOwnerIdAndId(Long ownerId, Long id);

    Optional<EventEntity> getEventEntityByIdAndState(Long id, StateEnum state);

    @Query(value = "select er from EventEntity er where er.eventDate > :datetime and er.id in (" +
            "select re.eventId from RequestEntity re where re.userId.id=:id and re.confirmed='CONFIRMED')")
    List<EventEntity> findAllByRequestEntities(@Param("id") Long id,
                                               @Param("datetime") LocalDateTime time,
                                               Pageable pageable);
}
