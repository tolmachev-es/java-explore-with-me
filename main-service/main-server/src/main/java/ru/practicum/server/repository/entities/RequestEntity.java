package ru.practicum.server.repository.entities;

import lombok.Data;
import ru.practicum.server.enums.RequestStatusEnum;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(schema = "PUBLIC", name = "REQUESTS")
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserEntity userId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EVENT_ID", nullable = false)
    private EventEntity eventId;
    @Column(name = "CONFIRMED", nullable = false)
    private RequestStatusEnum confirmed;
    @Column(name = "CREATED", nullable = false)
    private LocalDateTime created;
}
