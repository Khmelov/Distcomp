package com.socialnetwork.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_label", schema = "distcomp")
public class Label extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 32)
    @Size(min = 2, max = 32)
    private String name;

    @ManyToMany(mappedBy = "labels", fetch = FetchType.LAZY)
    private Set<Tweet> tweets = new HashSet<>();

    public Label() {
        super();
    }

    public Label(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(Set<Tweet> tweets) {
        this.tweets = tweets;
    }
}