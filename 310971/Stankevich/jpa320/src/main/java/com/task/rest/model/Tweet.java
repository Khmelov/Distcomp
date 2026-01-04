package com.task.rest.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_tweet")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"author", "marks", "notices"})
@NoArgsConstructor
@AllArgsConstructor
public class Tweet extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, length = 2048)
    private String content;

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "modified", nullable = false)
    private LocalDateTime modified;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "tbl_tweet_mark",
            joinColumns = @JoinColumn(name = "tweet_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "mark_id", nullable = false)
    )
    private List<Mark> marks = new ArrayList<>();

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> notices = new ArrayList<>();
}
