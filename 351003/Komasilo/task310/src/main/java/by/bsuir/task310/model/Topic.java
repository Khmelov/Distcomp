package by.bsuir.task310.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_topic")
@Data
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, length = 2048)
    private String content;

    private LocalDateTime created;

    private LocalDateTime modified;
}