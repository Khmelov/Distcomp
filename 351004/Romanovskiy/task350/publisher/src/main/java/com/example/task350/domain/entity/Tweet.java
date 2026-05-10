package com.example.task350.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_tweet")
@Data // ОБЯЗАТЕЛЬНО
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder 
public class Tweet extends BaseEntity {
    
    @Column(name = "author_id")
    private Long authorId;

    // ДОБАВЛЕНО: unique = true
    @Column(length = 64, nullable = false, unique = true) 
    private String title;

    @Column(length = 2048, nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime modified;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) // ДОЛЖНО БЫТЬ ТАК
    @JoinTable(
        name = "tbl_tweet_marker",
        joinColumns = @JoinColumn(name = "tweet_id"),
        inverseJoinColumns = @JoinColumn(name = "marker_id")
    )
    private List<Marker> markers = new ArrayList<>();
}