package ru.practicum.server.repository.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@SuperBuilder
@Table(schema = "PUBLIC", name = "FRIENDS")
public class FriendsEntity {
    @EmbeddedId
    private FriendsEntityId id;
    @ManyToOne
    @MapsId(value = "requestorId")
    @JoinColumn(name = "REQUESTOR_ID", nullable = false)
    private UserEntity requestor;
    @ManyToOne
    @MapsId(value = "friendId")
    @JoinColumn(name = "FRIEND_ID", nullable = false)
    private UserEntity friend;
    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendState state;

    public enum FriendState {PENDING, APPROVED, CANCEL}

    ;
}
