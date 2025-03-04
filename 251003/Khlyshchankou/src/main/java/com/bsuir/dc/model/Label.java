package com.bsuir.dc.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tbl_label")
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", unique = true)
    private String name;

    @ManyToMany()
    @JoinTable(name = "tbl_topic_label", joinColumns = {@JoinColumn (name = "labelId")}, inverseJoinColumns = {@JoinColumn (name = "topicId")})
    private List<Topic> topics = new ArrayList<>();

    public void setId(long id) { this.id = id; }
    public long getId() { return id; }

    public void setTopics(List<Topic> topics) { this.topics = topics; }
    public List<Topic> getTopics() { return topics; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Label label = (Label) o;
        return id == label.id && name.equals(label.name);
    }

    @Override
    public int hashCode() { return Objects.hash(id, name); }

    public Label(String name) { this.name = name; }

    public Label() {}
}
