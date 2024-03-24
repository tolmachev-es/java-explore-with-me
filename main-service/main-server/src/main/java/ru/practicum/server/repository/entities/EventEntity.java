package ru.practicum.server.repository.entities;

import lombok.Data;
import org.hibernate.annotations.Filter;
import ru.practicum.server.enums.StateEnum;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(schema = "PUBLIC", name = "EVENTS")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, unique = true)
    private Long id;
    @Column(name = "ANNOTATION", nullable = false)
    private String annotation;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private CategoryEntity category;
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;
    @Column(name = "EVENT_DATE", nullable = false)
    private LocalDateTime eventDate;
    @Column(name = "LIMIT_GUESTS")
    private Integer limit;
    @Column(name = "MODERATION")
    private Boolean moderation;
    @Column(name = "TITLE", nullable = false)
    private String title;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "OWNER_ID", nullable = false)
    private UserEntity owner;
    @Column(name = "CREATED_TIME")
    private LocalDateTime createdOn;
    @Column(name = "PUBLISHED_TIME")
    private LocalDateTime publishedOn;
    @Column(name = "STATUS")
    private StateEnum state;
    @Column(name = "PAID")
    private Boolean paid;
    @Column(name = "LAT")
    private Float locationLat;
    @Column(name = "LON")
    private Float locationLon;
    @OneToMany(fetch = FetchType.EAGER, targetEntity = RequestEntity.class)
    @JoinTable(
            name = "REQUESTS",
            joinColumns = @JoinColumn(name = "ID"),
            inverseJoinColumns = @JoinColumn(name = "EVENT_ID")
    )
    private List<RequestEntity> requestEntities = new ArrayList<>(0);

}
