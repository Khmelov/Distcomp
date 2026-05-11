package by.bsuir.task330.discussion.entity;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;

@PrimaryKeyClass
public class NoticeKey implements Serializable {

    @PrimaryKeyColumn(name = "article_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private Long articleId;

    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Long id;

    public NoticeKey() {
    }

    public NoticeKey(Long articleId, Long id) {
        this.articleId = articleId;
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NoticeKey noticeKey)) return false;
        return Objects.equals(articleId, noticeKey.articleId) && Objects.equals(id, noticeKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId, id);
    }
}
