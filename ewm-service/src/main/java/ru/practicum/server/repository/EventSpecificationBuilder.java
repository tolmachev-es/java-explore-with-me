package ru.practicum.server.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.server.enums.StateEnum;
import ru.practicum.server.models.AdminFilterParam;
import ru.practicum.server.models.PublicFilterParam;
import ru.practicum.server.repository.entities.EventEntity;
import ru.practicum.server.repository.entities.RequestEntity;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.List;

public class EventSpecificationBuilder {

    public static Specification<EventEntity> getEventSpecificationByPublicFilterParam(PublicFilterParam filterParam) {
        return Specification.where(textSpecification(filterParam.getText()))
                .and(categorySpecification(filterParam.getCategories()))
                .and(paidSpecification(filterParam.getPaid()))
                .and(availableSpecification(filterParam.getAvailable()))
                .and((filterParam.getEnd() == null || filterParam.getStart() == null) ?
                        startSpecification(LocalDateTime.now()) : startSpecification(filterParam.getStart()))
                .and((filterParam.getStart() == null || filterParam.getEnd() == null) ? null :
                        endSpecification(filterParam.getEnd()));
    }

    public static Specification<EventEntity> getEventSpecificationByAdminFilterParam(AdminFilterParam filterParam) {
        return Specification.where(ownerSpecification(filterParam.getUsers()))
                .and(categorySpecification(filterParam.getCategories()))
                .and(statesSpecification(filterParam.getStates()))
                .and(startSpecification(filterParam.getRangeStart()))
                .and(endSpecification(filterParam.getRangeEnd()));
    }

    public static Specification<EventEntity> ownerSpecification(List<Long> users) {
        if (users == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(root.get("owner").in(users));
    }

    public static Specification<EventEntity> categorySpecification(List<Long> categories) {
        if (categories == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(root.get("category").in(categories));
    }

    public static Specification<EventEntity> statesSpecification(List<StateEnum> states) {
        if (states == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(root.get("state").in(states));
    }

    public static Specification<EventEntity> startSpecification(LocalDateTime start) {
        if (start == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"),
                start);
    }

    public static Specification<EventEntity> endSpecification(LocalDateTime end) {
        if (end == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"),
                end);
    }

    public static Specification<EventEntity> textSpecification(String text) {
        if (text == null) {
            return null;
        }

        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("annotation"), "%" + text + "%"));
    }

    public static Specification<EventEntity> paidSpecification(Boolean paid) {
        if (paid == null) {
            return null;
        }
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid));
    }

    public static Specification<EventEntity> availableSpecification(Boolean available) {
        if (available == null || !available) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<RequestEntity> subRoot = subquery.from(RequestEntity.class);
            Predicate joinCondition = criteriaBuilder.equal(root.get("ID"), subRoot.get("EVENT_ID"));
            Predicate whereCondition = criteriaBuilder.equal(subRoot.get("CONFIRMED"), true);
            subquery.where(whereCondition, joinCondition);
            Predicate mainQueryCondition = criteriaBuilder.exists(subquery);
            query.where(mainQueryCondition);
            return null;
        };
    }
}
