package by.bsuir.discussion.entity;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;

@PrimaryKeyClass
public class MessageKey implements Serializable {

    @PrimaryKeyColumn(name = "story_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private Long storyId;

    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Long id;

    public MessageKey() {
    }

    public MessageKey(Long storyId, Long id) {
        this.storyId = storyId;
        this.id = id;
    }

    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(Long storyId) {
        this.storyId = storyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MessageKey key)) {
            return false;
        }
        return Objects.equals(storyId, key.storyId) && Objects.equals(id, key.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storyId, id);
    }
}
