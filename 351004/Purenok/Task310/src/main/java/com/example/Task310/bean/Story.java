package com.example.Task310.bean;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

@Entity
@Table(name = "tbl_story", schema = "distcomp")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime created;
    private LocalDateTime modified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id")
    private Editor editor;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    // ВАЖНО: Изменяем cascade на ALL, чтобы маркеры удалялись вместе с историей
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "tbl_story_marker",
            schema = "distcomp",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id")
    )
    private Set<Marker> markers = new HashSet<>();
}   