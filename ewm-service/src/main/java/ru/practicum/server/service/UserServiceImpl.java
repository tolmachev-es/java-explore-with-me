package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.server.dto.userDtos.NewUserRequestDao;
import ru.practicum.server.dto.userDtos.UserDto;
import ru.practicum.server.mappers.UserMapper;
import ru.practicum.server.models.User;
import ru.practicum.server.repository.UserDao;
import ru.practicum.server.service.interfaces.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userStorage;

    @Override
    public UserDto createNewUser(NewUserRequestDao user) {
        User newUser = UserMapper.USER_MAPPER.fromNewUserRequestDao(user);
        return UserMapper.USER_MAPPER.toUserDto(userStorage.createNewUser(newUser));
    }

    @Override
    public List<UserDto> getUsers(List<Long> userIds, Pageable pageable) {
        return userStorage.getUsers(userIds, pageable)
                .stream()
                .map(UserMapper.USER_MAPPER::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removeUserById(long userId) {
        userStorage.removeUserById(userId);
    }
}
