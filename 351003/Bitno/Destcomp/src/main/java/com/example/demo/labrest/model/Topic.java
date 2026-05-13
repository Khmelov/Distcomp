package com.example.demo.labrest.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
@Entity @Table(name = "tbl_topic")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Topic {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Creator creator;

    @Column(nullable = false, length = 64) private String title;
    @Column(nullable = false, length = 2048) private String content;
    @Column(nullable = false) private LocalDateTime created;
    @Column(nullable = false) private LocalDateTime modified;

    @ManyToMany
    @JoinTable(name = "tbl_topic_marker",
            schema = "distcomp",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id"))
    private Set<Marker> markers = new HashSet<>();
    public Long getCreatorId() {
        return creator.getId();
    }
    public List<Long> getMarkerIds() {
        ArrayList<Long> ids = new ArrayList();
        for (Marker marker: markers) {
            ids.add(marker.getId());
        }
        return ids;
    }
}