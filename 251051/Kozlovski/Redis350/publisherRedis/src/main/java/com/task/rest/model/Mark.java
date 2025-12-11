package com.task.rest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_mark")
public class Mark extends BaseEntity {
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 32, message = "Name must be at most 32 characters")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "marks")
    private List<Tweet> tweets = new ArrayList<>();

    public Mark() {}

    public Mark(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Tweet> getTweets() { return tweets; }
    public void setTweets(List<Tweet> tweets) { this.tweets = tweets; }
}
