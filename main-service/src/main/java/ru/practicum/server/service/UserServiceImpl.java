package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.server.dto.NewUserRequestDao;
import ru.practicum.server.dto.UserDto;
import ru.practicum.server.mappers.UserMapper;
import ru.practicum.server.models.User;
import ru.practicum.server.repository.UserDB;
import ru.practicum.server.service.interfaces.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDB userStorage;

    @Override
    public ResponseEntity<?> createNewUser(NewUserRequestDao user) {
        User newUser = UserMapper.USER_MAPPER.fromNewUserRequestDao(user);
        return new ResponseEntity<>(UserMapper.USER_MAPPER.toUserDto(
                userStorage.createNewUser(newUser)), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getUsers(List<Long> userIds, Pageable pageable) {
        List<UserDto> userDtoList = userStorage.getUsers(userIds, pageable)
                .stream()
                .map(UserMapper.USER_MAPPER::toUserDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDtoList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> removeUserById(long userId) {
        userStorage.removeUserById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
