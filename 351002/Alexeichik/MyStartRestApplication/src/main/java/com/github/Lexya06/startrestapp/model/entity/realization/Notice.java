package com.github.Lexya06.startrestapp.model.entity.realization;

import com.github.Lexya06.startrestapp.model.entity.abstraction.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "notices")
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notices_seq")
    @SequenceGenerator(name = "notices_seq", sequenceName = "notices_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(length = 2048)
    private String content;
}
