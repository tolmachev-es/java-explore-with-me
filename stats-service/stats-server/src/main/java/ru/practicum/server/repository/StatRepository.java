package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface StatRepository extends JpaRepository<HitEntity, Long> {
    @Query("select e.uri, e.app, e.ip, count(e) as count " +
            "from HitEntity e where e.timestamp between :start and :end " +
            "and (:uris is null or e.uri in :uris) " +
            "group by e.uri, e.app, e.ip")
    List<Object[]> findAllStatistic(LocalDateTime start, LocalDateTime end, List<String> uris);
}
