package ru.practicum.server.repository.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(schema = "PUBLIC", name = "CATEGORY")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, unique = true)
    private Long id;
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;
}
