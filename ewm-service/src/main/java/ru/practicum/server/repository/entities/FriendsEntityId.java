package ru.practicum.server.repository.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@SuperBuilder
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class FriendsEntityId implements Serializable {
    @Column(name = "REQUESTOR_ID", nullable = false)
    private Long requestorId;
    @Column(name = "FRIEND_ID", nullable = false)
    private Long friendId;
}
