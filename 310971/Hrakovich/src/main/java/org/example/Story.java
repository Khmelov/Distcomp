package org.example;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(
        name = "tbl_story",
        uniqueConstraints = @UniqueConstraint(columnNames = "title")
)
public class Story extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Writer writer;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StoryTag> storyTags = new HashSet<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> notices = new ArrayList<>();

    // getters / setters

    // helpers
    public void addTag(Tag tag) {
        StoryTag st = new StoryTag(this, tag);
        storyTags.add(st);
        tag.getStoryTags().add(st);
    }

    public void removeTag(Tag tag) {
        storyTags.removeIf(st -> st.getTag().equals(tag));
        tag.getStoryTags().removeIf(st -> st.getStory().equals(this));
    }
}