package ru.practicum.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.dto.categoryDtos.CategoryDto;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.mappers.EventMapper;
import ru.practicum.server.models.Category;
import ru.practicum.server.repository.entities.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryDao {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(Category category) {
        CategoryEntity categoryEntity = EventMapper.EVENT_MAPPER.toCategoryEntity(category);
        categoryRepository.save(categoryEntity);
        return EventMapper.EVENT_MAPPER.fromCategoryEntity(categoryEntity);
    }

    @Transactional
    public Category getCategoryById(Long catId) {
        Optional<CategoryEntity> category = categoryRepository.findById(catId);
        if (category.isPresent()) {
            return EventMapper.EVENT_MAPPER.fromCategoryEntity(category.get());
        } else {
            throw new NotFoundException(String.format("Category with id=%s not found", catId));
        }
    }

    @Transactional
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

    @Transactional
    public void deleteCategory(Long categoryId) {
        try {
            categoryRepository.deleteById(categoryId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Category with id=%s not found", categoryId));
        }
    }

    @Transactional
    public List<Category> getPageableCategory(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .toList()
                .stream()
                .map(EventMapper.EVENT_MAPPER::fromCategoryEntity)
                .collect(Collectors.toList());
    }
}
