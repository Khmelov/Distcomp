package com.github.Lexya06.startrestapp.model.entity.realization;

import com.github.Lexya06.startrestapp.model.entity.abstraction.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "tbl_article")
public class Article extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "articles_seq")
    @SequenceGenerator(name = "articles_seq", sequenceName = "articles_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 64, unique = true)
    private String title;

    @Column(length = 2048)
    private String content;

    @CreationTimestamp
    private OffsetDateTime created;

    @UpdateTimestamp
    private OffsetDateTime modified;

    @OneToMany(mappedBy = "article", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Notice> notices = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_article_m2m_label",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();
}
