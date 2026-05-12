package by.bsuir.task330.discussion.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_notice")
public class NoticeEntity {

    @PrimaryKey
    private NoticeKey key;

    private String content;

    public NoticeEntity() {
    }

    public NoticeEntity(NoticeKey key, String content) {
        this.key = key;
        this.content = content;
    }

    public NoticeKey getKey() {
        return key;
    }

    public void setKey(NoticeKey key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
