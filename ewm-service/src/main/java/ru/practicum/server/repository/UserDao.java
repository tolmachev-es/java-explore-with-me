package ru.practicum.server.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.exceptions.AlreadyUseException;
import ru.practicum.server.exceptions.IncorrectRequestException;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.mappers.UserMapper;
import ru.practicum.server.models.User;
import ru.practicum.server.repository.entities.FriendsEntity;
import ru.practicum.server.repository.entities.FriendsEntityId;
import ru.practicum.server.repository.entities.SubscribeEntity;
import ru.practicum.server.repository.entities.UserEntity;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDao {
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;
    private final SubscribeRepository subscribeRepository;

    @Transactional
    public User createNewUser(User user) {
        UserEntity userEntity = UserMapper.USER_MAPPER.toEntity(user);
        try {
            userRepository.save(userEntity);
            return UserMapper.USER_MAPPER.fromEntity(userEntity);
        } catch (ConstraintViolationException e) {
            throw new AlreadyUseException(e.getMessage());
        }
    }

    @Transactional
    public List<User> getUsers(List<Long> userIds, Pageable pageable) {
        Specification<UserEntity> specification = Specification
                .where(userIds.isEmpty() ? null :
                        (root, query, criteriaBuilder) -> root.get("id").in(userIds));
        return userRepository.findAll(specification, pageable).stream()
                .map(UserMapper.USER_MAPPER::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeUserById(long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("User with id=%s was not found", userId));
        }
    }

    @Transactional
    public User getUserById(long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return UserMapper.USER_MAPPER.fromEntity(user.get());
        } else {
            throw new NotFoundException(String.format("User with id=%s not found", userId));
        }
    }

    @Transactional
    public void addFriend(Long userId, Long friendId) {
        Optional<FriendsEntity> friendsEntity = friendsRepository
                .findById(FriendsEntityId.builder()
                        .requestorId(userId)
                        .friendId(friendId)
                        .build());
        if (friendsEntity.isPresent()) {
            throw new AlreadyUseException(String.format("User %s already send request to user %s", userId, friendId));
        } else {
            User user = getUserById(userId);
            User friend = getUserById(friendId);
            FriendsEntity newFriendsEntity = FriendsEntity.builder()
                    .id(FriendsEntityId.builder()
                            .requestorId(userId)
                            .friendId(friendId)
                            .build())
                    .requestor(UserMapper.USER_MAPPER.toEntity(user))
                    .friend(UserMapper.USER_MAPPER.toEntity(friend))
                    .state(FriendsEntity.FriendState.PENDING)
                    .build();
            friendsRepository.save(newFriendsEntity);
        }
    }

    @Transactional
    public void changeFriendRequestStatus(Long userId, Long friendId, FriendsEntity.FriendState state) {
        Optional<FriendsEntity> friendsEntity = friendsRepository
                .findById(FriendsEntityId.builder().requestorId(friendId).friendId(userId).build());
        if (friendsEntity.isPresent()) {
            FriendsEntity friends = friendsEntity.get();
            if (state.equals(FriendsEntity.FriendState.APPROVED)) {
                if (friends.getState().equals(FriendsEntity.FriendState.APPROVED)) {
                    throw new AlreadyUseException("Users already friends");
                } else {
                    friends.setState(FriendsEntity.FriendState.APPROVED);
                }
            } else if (state.equals(FriendsEntity.FriendState.CANCEL)) {
                if (friends.getState().equals(FriendsEntity.FriendState.CANCEL)) {
                    throw new AlreadyUseException("User already cancel friend request");
                } else {
                    friends.setState(FriendsEntity.FriendState.CANCEL);
                }
            } else {
                throw new IncorrectRequestException("Method not found");
            }
            friendsRepository.save(friends);
        } else {
            throw new NotFoundException("Friends request not found");
        }
    }

    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        Optional<FriendsEntity> friendsEntity = friendsRepository
                .findFriendsEntityByIdAndStateOrIdAndState(FriendsEntityId.builder().requestorId(userId).friendId(friendId).build(),
                        FriendsEntity.FriendState.APPROVED,
                        FriendsEntityId.builder().requestorId(friendId).friendId(userId).build(),
                        FriendsEntity.FriendState.APPROVED);
        if (friendsEntity.isPresent()) {
            FriendsEntity friends = friendsEntity.get();
            if (friends.getId().getRequestorId().equals(userId)) {
                friends.setState(FriendsEntity.FriendState.PENDING);
            } else {
                friendsRepository.delete(friends);
                addFriend(friendId, userId);
            }
        } else {
            throw new NotFoundException(String.format("User with id=%s and user with id=%s not friend", userId, friendId));
        }
    }

    public FriendsEntity getFriendship(Long userId, Long friendId) {
        Optional<FriendsEntity> friendsEntity = friendsRepository
                .findFriendsEntityByIdAndStateOrIdAndState(FriendsEntityId.builder().requestorId(userId).friendId(friendId).build(),
                        FriendsEntity.FriendState.APPROVED,
                        FriendsEntityId.builder().requestorId(friendId).friendId(userId).build(),
                        FriendsEntity.FriendState.APPROVED);
        if (friendsEntity.isPresent()) {
            return friendsEntity.get();
        } else {
            throw new NotFoundException(String.format("User with id=%s and user with id=%s not friend", userId, friendId));
        }
    }

    @Transactional
    public List<UserEntity> getOutboxFriendRequest(Long userId, Pageable pageable) {
        return friendsRepository.findFriendsEntitiesByRequestor_IdAndState(userId, FriendsEntity.FriendState.PENDING, pageable)
                .stream()
                .map(FriendsEntity::getFriend)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UserEntity> getInboxFriendRequest(Long userId, Pageable pageable) {
        return friendsRepository.findFriendsEntitiesByFriend_IdAndState(userId, FriendsEntity.FriendState.PENDING, pageable)
                .stream()
                .map(FriendsEntity::getRequestor)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UserEntity> getFriends(Long userId, Pageable pageable) {
        return friendsRepository.findFriendsEntitiesByRequestor_IdAndStateOrFriend_IdAndState(userId,
                        FriendsEntity.FriendState.APPROVED,
                        userId,
                        FriendsEntity.FriendState.APPROVED,
                        pageable).stream()
                .map(f -> f.getId().getFriendId().equals(userId) ? f.getRequestor() : f.getFriend())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFriendRequest(Long userId, Long friendId) {
        Optional<FriendsEntity> friendsEntity = friendsRepository
                .findById(FriendsEntityId.builder()
                        .requestorId(userId)
                        .friendId(friendId).build());
        if (friendsEntity.isPresent()) {
            FriendsEntity friends = friendsEntity.get();
            if (friends.getState().equals(FriendsEntity.FriendState.APPROVED)) {
                throw new IncorrectRequestException(String.format("User %s and user %s already friend, \n" +
                        "the request cannot be deleted"));
            } else {
                friendsRepository.delete(friends);
            }
        } else {
            throw new NotFoundException(String.format("Request from user %s to user %s not found", userId, friendId));
        }
    }

    @Transactional
    public void subscribe(Long userId, Long curatorId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        Optional<UserEntity> curator = userRepository.findById(curatorId);
        if (user.isPresent() && curator.isPresent()) {
            Optional<SubscribeEntity> subscribe = subscribeRepository.findByCurator_IdAndSubscriber_Id(curatorId, userId);
            if (subscribe.isEmpty()) {
                subscribeRepository.save(SubscribeEntity.builder()
                        .subscriber(user.get())
                        .curator(curator.get())
                        .build());
            } else {
                throw new AlreadyUseException("Already has subscription");
            }
        } else {
            throw new NotFoundException("User or curator not found");
        }
    }

    @Transactional
    public void unsubscription(Long userId, Long curatorId) {
        Optional<SubscribeEntity> subscribe = subscribeRepository.findByCurator_IdAndSubscriber_Id(curatorId, userId);
        if (subscribe.isPresent()) {
            subscribeRepository.delete(subscribe.get());
        } else {
            throw new NotFoundException("Subscription not found");
        }
    }

    @Transactional
    public List<User> getSubscription(Long userId, Pageable pageable) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return subscribeRepository.getAllBySubscriber_Id(userId, pageable)
                    .stream()
                    .map(SubscribeEntity::getCurator)
                    .map(UserMapper.USER_MAPPER::fromEntity)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Transactional
    public List<Long> getCurator(Long userId, List<Long> curators) {
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return subscribeRepository.getAllBySubscriber_IdAndCurator_Id(userId, curators)
                    .stream()
                    .map(SubscribeEntity::getCurator)
                    .map(UserEntity::getId)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("User not found");
        }
    }
}
