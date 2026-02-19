package com.distcomp.model.note;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.Size;
import lombok.*;

@Table(name = "tbl_note", schema = "distcomp")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    @Id
    private Long id;

    @Size(min = 2, max = 2048)
    private String content;

    @Column("topic_id")
    private Long topicId;
}