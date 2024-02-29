package ru.practicum.server.repository;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(schema = "PUBLIC", name = "STATISTIC")
public class HitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, unique = true)
    private Long id;
    @Column(name = "APPLICATION", nullable = false)
    private String app;
    @Column(name = "URI", nullable = false)
    private String uri;
    @Column(name = "IP", nullable = false)
    private String ip;
    @Column(name = "REQUEST_TIME", nullable = false)
    private LocalDateTime timestamp;
}
