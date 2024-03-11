package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.repository.entities.CategoryEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
