package org.rv.lab1.discussion.domain;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;


import java.io.Serializable;
import java.util.Objects;

@PrimaryKeyClass
public class CommentByStoryKey implements Serializable {
    @PrimaryKeyColumn(name = "story_id", type = PrimaryKeyType.PARTITIONED)
    private Long storyId;

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED)
    private Long id;

    protected CommentByStoryKey() {
    }

    public CommentByStoryKey(Long storyId, Long id) {
        this.storyId = storyId;
        this.id = id;
    }

    public Long getStoryId() {
        return storyId;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentByStoryKey that = (CommentByStoryKey) o;
        return Objects.equals(storyId, that.storyId) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storyId, id);
    }
}

