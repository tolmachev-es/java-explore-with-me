package ru.practicum.server.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.repository.entities.FriendsEntity;
import ru.practicum.server.repository.entities.FriendsEntityId;

import java.util.List;
import java.util.Optional;

public interface FriendsRepository extends JpaRepository<FriendsEntity, FriendsEntityId> {
    Optional<FriendsEntity> findFriendsEntityByIdAndStateOrIdAndState(FriendsEntityId id,
                                                                      FriendsEntity.FriendState state,
                                                                      FriendsEntityId id2,
                                                                      FriendsEntity.FriendState state2);

    List<FriendsEntity> findFriendsEntitiesByRequestor_IdAndState(Long requestorId,
                                                                  FriendsEntity.FriendState state,
                                                                  Pageable pageable);

    List<FriendsEntity> findFriendsEntitiesByFriend_IdAndState(Long friendId,
                                                               FriendsEntity.FriendState state,
                                                               Pageable pageable);

    List<FriendsEntity> findFriendsEntitiesByRequestor_IdAndStateOrFriend_IdAndState(Long requestorId,
                                                                                     FriendsEntity.FriendState state,
                                                                                     Long friendId,
                                                                                     FriendsEntity.FriendState state2,
                                                                                     Pageable pageable);
}
