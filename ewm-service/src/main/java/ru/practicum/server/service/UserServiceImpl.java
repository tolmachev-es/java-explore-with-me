package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.server.dto.userDtos.NewUserRequestDao;
import ru.practicum.server.dto.userDtos.UserDto;
import ru.practicum.server.mappers.UserMapper;
import ru.practicum.server.models.User;
import ru.practicum.server.repository.UserDao;
import ru.practicum.server.repository.entities.FriendsEntity;
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

    @Override
    public void addFriend(Long userId, Long friendId) {
        userStorage.addFriend(userId, friendId);
    }

    @Override
    public void changeFriendRequestStatus(Long userId, Long friendId, FriendsEntity.FriendState state) {
        userStorage.changeFriendRequestStatus(userId, friendId, state);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    @Override
    public List<UserDto> getOutboxFriendRequest(Long userId, Pageable pageable) {
        return userStorage.getOutboxFriendRequest(userId, pageable).stream()
                .map(UserMapper.USER_MAPPER::fromEntity)
                .map(UserMapper.USER_MAPPER::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getInboxFriendRequest(Long userId, Pageable pageable) {
        return userStorage.getInboxFriendRequest(userId, pageable).stream()
                .map(UserMapper.USER_MAPPER::fromEntity)
                .map(UserMapper.USER_MAPPER::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getFriends(Long userId, Pageable pageable) {
        return userStorage.getFriends(userId, pageable).stream()
                .map(UserMapper.USER_MAPPER::fromEntity)
                .map(UserMapper.USER_MAPPER::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFriendRequest(Long userId, Long friendId) {
        userStorage.deleteFriendRequest(userId, friendId);
    }

    @Override
    public void subscribe(Long userId, Long curatorId) {
        userStorage.subscribe(userId, curatorId);
    }

    @Override
    public void unsubscription(Long userId, Long curatorId) {
        userStorage.unsubscription(userId, curatorId);
    }

    @Override
    public List<UserDto> getSubsctiption(Long userId, Pageable pageable) {
        return userStorage.getSubscription(userId, pageable)
                .stream()
                .map(UserMapper.USER_MAPPER::toUserDto)
                .collect(Collectors.toList());
    }
}
