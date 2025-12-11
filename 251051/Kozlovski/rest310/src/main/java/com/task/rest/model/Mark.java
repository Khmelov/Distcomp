package com.task.rest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "marks")
public class Mark extends BaseEntity {
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 32, message = "Name must be at most 32 characters")
    private String name;

    @ManyToMany(mappedBy = "marks")
    private Set<Tweet> tweets = new HashSet<>();

    public Mark() {}

    public Mark(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<Tweet> getTweets() { return tweets; }
    public void setTweets(Set<Tweet> tweets) { this.tweets = tweets; }
}
