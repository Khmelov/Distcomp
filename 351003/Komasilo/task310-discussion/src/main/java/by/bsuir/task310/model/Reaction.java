package by.bsuir.task310.model;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_reaction")
@Data
public class Reaction {

    @PrimaryKey
    private Long id;

    private Long topicId;

    private String content;
}