package org.example;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StoryTagId implements Serializable {

    @Column(name = "story_id")
    private Long storyId;

    @Column(name = "tag_id")
    private Long tagId;

    public StoryTagId() {}

    public StoryTagId(Long storyId, Long tagId) {
        this.storyId = storyId;
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoryTagId)) return false;
        StoryTagId that = (StoryTagId) o;
        return Objects.equals(storyId, that.storyId) &&
                Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storyId, tagId);
    }
}
