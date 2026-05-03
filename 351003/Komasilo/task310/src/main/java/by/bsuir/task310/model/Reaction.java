package by.bsuir.task310.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_reaction")
@Data
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long topicId;

    @Column(nullable = false, length = 2048)
    private String content;
}