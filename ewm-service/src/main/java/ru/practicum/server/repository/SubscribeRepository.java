package ru.practicum.server.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.server.repository.entities.SubscribeEntity;

import java.util.List;
import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<SubscribeEntity, Long> {
    Optional<SubscribeEntity> findByCurator_IdAndSubscriber_Id(Long curatorId, Long subscriberId);

    List<SubscribeEntity> getAllBySubscriber_Id(Long subscriberId, Pageable pageable);

    @Query(value = "select s from SubscribeEntity s where s.subscriber.id=:id " +
            "and (coalesce(:curators) is null or s.curator.id in (:curators) )")
    List<SubscribeEntity> getAllBySubscriber_IdAndCurator_Id(@Param("id") Long id,
                                                             @Param("curators") List<Long> curators);
}
