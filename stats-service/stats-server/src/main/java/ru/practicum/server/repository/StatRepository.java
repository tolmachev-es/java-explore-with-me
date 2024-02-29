package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface StatRepository extends JpaRepository<HitEntity, Long> {
    @Query("select e.uri as uri, e.app as app, e.ip as ip, count(e) as count " +
            "from HitEntity e where e.timestamp between :start and :end " +
            "and (coalesce(:uris) is null or e.uri in (:uris) ) " +
            "group by e.uri, e.app, e.ip")
    List<ViewStatsProjections> findAllStatistic(LocalDateTime start, LocalDateTime end, @Param("uris") List<String> uris);
}
