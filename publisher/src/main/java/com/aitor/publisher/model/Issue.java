package com.aitor.publisher.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "tbl_issue")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User userId;
    @NonNull
    @Column(unique = true, nullable = false)
    String title;
    @NonNull
    String content;
    @NonNull
    LocalDateTime created;
    @NonNull
    LocalDateTime modified;
}