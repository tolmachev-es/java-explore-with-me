package ru.practicum.server.repository.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(schema = "PUBLIC", name = "COMPILATIONS")
public class CompilationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "TITLE", nullable = false)
    private String title;
    @Column(name = "PINNED", nullable = false)
    private Boolean pinned;
    @ManyToMany(cascade = {CascadeType.DETACH})
    @JoinTable(
            name = "COMPILATION_EVENTS",
            joinColumns = @JoinColumn(name = "COMPILATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "EVENT_ID")
    )
    private List<EventEntity> eventEntities = new ArrayList<>();
}
