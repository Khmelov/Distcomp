package com.distcomp.discussion.post.domain;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_post")
public class Post {

    @PrimaryKey
    private PostKey key;

    private String content;

    public Post() {
    }

    public Post(PostKey key, String content) {
        this.key = key;
        this.content = content;
    }

    public PostKey getKey() {
        return key;
    }

    public void setKey(PostKey key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
