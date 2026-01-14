package org.example;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_story_tag")
public class StoryTag {

    @EmbeddedId
    private StoryTagId id = new StoryTagId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("storyId")
    @JoinColumn(name = "story_id")
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;

    protected StoryTag() {}

    public StoryTag(Story story, Tag tag) {
        this.story = story;
        this.tag = tag;
        this.id = new StoryTagId(story.getId(), tag.getId());
    }

    public Tag getTag() {   // ВАЖНО для MapStruct
        return tag;
    }

    public Story getStory() {   // ВАЖНО для MapStruct
        return story;
    }
}
