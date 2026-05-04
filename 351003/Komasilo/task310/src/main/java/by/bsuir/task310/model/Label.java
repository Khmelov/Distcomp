package by.bsuir.task310.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_label")
@Data
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(name = "topic_id")
    private Long topicId;
}