package by.bsuir.distcomp.discussion.domain;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_reaction")
public class Reaction {

    @PrimaryKey
    private ReactionKey key;

    @Column("content")
    private String content;

    public ReactionKey getKey() {
        return key;
    }

    public void setKey(ReactionKey key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
