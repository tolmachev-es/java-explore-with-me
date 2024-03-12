package ru.practicum.server.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.server.models.FilterParam;
import ru.practicum.server.repository.entities.EventEntity;

public class EventSpecification {
    public static Specification<EventEntity> ownerSpecification(FilterParam filterParam) {
        if (filterParam.getUsers().isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(root.get("OWNER_ID").in(filterParam.getUsers()));
    }

    public static Specification<EventEntity> categorySpecification(FilterParam filterParam) {
        if (filterParam.getCategories().isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(root.get("CATEGORY_ID").in(filterParam.getCategories()));
    }

    public static Specification<EventEntity> statesSpecification(FilterParam filterParam) {
        if (filterParam.getStates().isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(root.get("STATUS").in(filterParam.getStates()));
    }

    public static Specification<EventEntity> startSpecification(FilterParam filterParam) {
        if (filterParam.getRangeStart() == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("EVENT_DATE"),
                filterParam.getRangeStart());
    }

    public static Specification<EventEntity> endSpecification(FilterParam filterParam) {
        if (filterParam.getRangeEnd() == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("EVENT_DATE"),
                filterParam.getRangeEnd());
    }
}
