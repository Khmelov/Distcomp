package com.lizaveta.discussion.cassandra;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;

@PrimaryKeyClass
public class NoticeByStoryKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKeyColumn(name = "story_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private Long storyId;

    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private Long id;

    public NoticeByStoryKey() {
    }

    public NoticeByStoryKey(final Long storyId, final Long id) {
        this.storyId = storyId;
        this.id = id;
    }

    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(final Long storyId) {
        this.storyId = storyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NoticeByStoryKey that = (NoticeByStoryKey) o;
        return Objects.equals(storyId, that.storyId) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storyId, id);
    }
}
