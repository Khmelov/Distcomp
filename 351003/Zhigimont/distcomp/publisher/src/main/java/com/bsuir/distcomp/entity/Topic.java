package com.bsuir.distcomp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tbl_topic")
@Data
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private LocalDateTime created;

    private LocalDateTime modified;

    @ManyToOne
    @JoinColumn(name = "writer_id")
    private Writer writer;


    @ManyToMany
    @JoinTable(
            name = "tbl_topic_marker",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id")
    )

    private List<Marker> markers;
}

