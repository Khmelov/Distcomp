package by.molchan.Discussion.models;


import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.io.Serializable;


@Table("tbl_comment")
public class Comment implements Serializable {

    @PrimaryKeyClass
    public static class CommentKey implements Serializable {
        @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED)
        private String country = "Republic Of Belarus";

        @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
        private Long id;

        @PrimaryKeyColumn(name = "articleId", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
        private Long articleId;


        public CommentKey() {
        }

        public CommentKey(String country, Long articleId, Long id) {
            this.country = country;
            this.articleId = articleId;
            this.id = id;
        }

        public CommentKey(Long articleId) {
            this.articleId = articleId;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
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

    }


    @Column("content")
    private String content;

    @PrimaryKey
    private CommentKey key;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CommentKey getKey() {
        return key;
    }

    public void setKey(CommentKey key) {
        this.key = key;
    }
}