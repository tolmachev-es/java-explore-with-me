package ru.practicum.server.repository;

import lombok.RequiredArgsConstructor;
import org.postgresql.util.PSQLException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.server.dto.CategoryDto;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.mappers.EventMapper;
import ru.practicum.server.models.Category;
import ru.practicum.server.repository.entities.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryDB {
    private final CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        CategoryEntity categoryEntity = EventMapper.EVENT_MAPPER.toCategoryEntity(category);
        categoryRepository.save(categoryEntity);
        return EventMapper.EVENT_MAPPER.fromCategoryEntity(categoryEntity);
    }

    public Category getCategoryById(Long catId) {
        Optional<CategoryEntity> category = categoryRepository.findById(catId);
        if (category.isPresent()) {
            return EventMapper.EVENT_MAPPER.fromCategoryEntity(category.get());
        } else {
            throw new NotFoundException(String.format("Category with id=%s not found", catId));
        }
    }

    public Category updateCategory(CategoryDto categoryDto, Long categoryId) {
        Optional<CategoryEntity> oldCategory = categoryRepository.findById(categoryId);
        if (oldCategory.isPresent()) {
            oldCategory.get().setName(categoryDto.getName());
            categoryRepository.save(oldCategory.get());
            return EventMapper.EVENT_MAPPER.fromCategoryEntity(oldCategory.get());
        } else {
            throw new NotFoundException(String.format("Category with id=%s not found", categoryId));
        }
    }

    public void deleteCategory(Long categoryId) {
        try {
            categoryRepository.deleteById(categoryId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Category with id=%s not found", categoryId));
        }
    }

    public List<Category> getPageableCategory(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .toList()
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromCategoryEntity)
                .collect(Collectors.toList());
    }
}
