package ru.practicum.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.server.exceptions.AlreadyUseException;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.mappers.UserMapper;
import ru.practicum.server.models.User;
import ru.practicum.server.repository.entities.UserEntity;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDB {
    private final UserRepository userRepository;

    public User createNewUser(User user) {
        UserEntity userEntity = UserMapper.USER_MAPPER.toEntity(user);
        try {
            userRepository.save(userEntity);
            return UserMapper.USER_MAPPER.fromEntity(userEntity);
        } catch (ConstraintViolationException e) {
            throw new AlreadyUseException(e.getMessage());
        }
    }

    public List<User> getUsers(List<Long> userIds, Pageable pageable) {
        Specification<UserEntity> specification = Specification
                .where(userIds.isEmpty() ? null :
                        (root, query, criteriaBuilder) -> root.get("id").in(userIds));
        return userRepository.findAll(specification, pageable).stream()
                .map(UserMapper.USER_MAPPER::fromEntity)
                .collect(Collectors.toList());
    }

    public void removeUserById(long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("User with id=%s was not found", userId));
        }
    }

    public User getUserById(long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return UserMapper.USER_MAPPER.fromEntity(user.get());
        } else {
            throw new NotFoundException(String.format("User with id=%s not found", userId));
        }
    }
}
