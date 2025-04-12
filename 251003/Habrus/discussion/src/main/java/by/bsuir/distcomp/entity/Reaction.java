package by.bsuir.distcomp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("tbl_reaction")
public class Reaction {

    @PrimaryKey
    private ReactionKey key;

    @Column
    private Long articleId;

    @Column
    private String content;

}
