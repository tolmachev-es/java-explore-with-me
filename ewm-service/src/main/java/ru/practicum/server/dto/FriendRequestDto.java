package ru.practicum.server.dto;

import ru.practicum.server.repository.UserDao;
import ru.practicum.server.repository.entities.FriendsEntity;

public class FriendRequestDto {
    private UserDao user;
    private FriendsEntity.FriendState state;
}
