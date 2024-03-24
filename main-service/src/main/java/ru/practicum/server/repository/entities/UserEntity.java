package ru.practicum.server.repository.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(schema = "PUBLIC", name = "USERS")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, unique = true)
    private Long id;
    @Column(name = "NAME", nullable = false)
    private String name;
    @Column(name = "EMAIL", nullable = false)
    private String email;
}
