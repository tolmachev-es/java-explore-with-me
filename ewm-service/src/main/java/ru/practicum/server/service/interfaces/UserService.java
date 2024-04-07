package ru.practicum.server.service.interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.server.dto.userDtos.NewUserRequestDao;
import ru.practicum.server.dto.userDtos.UserDto;
import ru.practicum.server.repository.entities.FriendsEntity;

import java.util.List;

@Service
public interface UserService {
    UserDto createNewUser(NewUserRequestDao userDto);

    List<UserDto> getUsers(List<Long> userIds, Pageable pageable);

    void removeUserById(long userId);

    void addFriend(Long userId, Long friendId);

    void changeFriendRequestStatus(Long userId, Long friendId, FriendsEntity.FriendState state);

    void deleteFriend(Long userId, Long friendId);

    List<UserDto> getOutboxFriendRequest(Long userId, Pageable pageable);

    List<UserDto> getInboxFriendRequest(Long userId, Pageable pageable);

    List<UserDto> getFriends(Long userId, Pageable pageable);

    void deleteFriendRequest(Long userId, Long friendId);

    void subscribe(Long userId, Long curatorId);

    void unsubscription(Long userId, Long curatorId);

    List<UserDto> getSubsctiption(Long userId, Pageable pageable);
}
