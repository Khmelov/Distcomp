package com.example.task310.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tbl_issue")
@Data @NoArgsConstructor @AllArgsConstructor
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "writer_id", nullable = false)
    private Long writerId;

    @Column(name = "title", nullable = false, length = 64, unique = true)
    private String title;

    @Column(name = "content", nullable = false, length = 2048)
    private String content;

    private LocalDateTime created;
    private LocalDateTime modified;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "tbl_issue_marker",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id")
    )
    private List<Marker> markers;
}