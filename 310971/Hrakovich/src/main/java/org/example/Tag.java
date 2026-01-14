package org.example;

import jakarta.persistence.*;
import lombok.Getter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Table(name = "tbl_tag")
public class Tag extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "tag",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<StoryTag> storyTags = new HashSet<>();

    protected Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Set<StoryTag> getStoryTags() {
        return storyTags;
    }
}