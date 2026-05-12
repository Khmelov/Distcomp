package by.bsuir.task340.discussion.domain;

import by.bsuir.task340.discussion.dto.NoticeState;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_notice")
public class Notice {

    @PrimaryKey
    private Long id;

    private Long articleId;
    private String content;
    private NoticeState state;

    public Notice() {
    }

    public Notice(Long id, Long articleId, String content, NoticeState state) {
        this.id = id;
        this.articleId = articleId;
        this.content = content;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NoticeState getState() {
        return state;
    }

    public void setState(NoticeState state) {
        this.state = state;
    }
}