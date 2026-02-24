package org.example.newsapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_news")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_seq_gen")
    @SequenceGenerator(name = "news_seq_gen", sequenceName = "news_seq", allocationSize = 1)
    private Long id;

    // Связь многие-к-одному с User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, length = 2048)
    private String content;

    @CreationTimestamp // Автоматически ставит дату при создании
    private LocalDateTime created;

    @UpdateTimestamp // Автоматически обновляет дату при изменении
    private LocalDateTime modified;

    // Связь один-ко-многим с комментариями
    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Comment> comments;

    // Связь многие-ко-многим с маркерами через промежуточную таблицу
    @ManyToMany(fetch = FetchType.EAGER) // Поставь EAGER для тестов, чтобы маппер всегда видел маркеры
    @JoinTable(
            name = "tbl_news_marker",
            joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id")
    )
    private Set<Marker> markers = new HashSet<>();
}