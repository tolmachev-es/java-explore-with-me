package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.server.repository.entities.CompilationEntity;

public interface CompilationRepository extends JpaRepository<CompilationEntity, Long>,
        JpaSpecificationExecutor<CompilationEntity> {

}
