package com.socialnetwork.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_tweet", schema = "distcomp")
public class Tweet extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 64)
    @Size(min = 2, max = 64)
    private String title;

    @Column(name = "content", nullable = false, length = 2048)
    @Size(min = 4, max = 2048)
    private String content;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_tweet_label",
            schema = "distcomp",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private Set<Label> labels = new HashSet<>();

    // Удаляем связь с Message, так как сообщения теперь в отдельном модуле
    // @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Set<Message> messages = new HashSet<>();

    public Tweet() {
        super();
    }

    public void addLabel(Label label) {
        labels.add(label);
        label.getTweets().add(this);
    }

    public void removeLabel(Label label) {
        labels.remove(label);
        label.getTweets().remove(this);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<Label> getLabels() {
        return labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }
}