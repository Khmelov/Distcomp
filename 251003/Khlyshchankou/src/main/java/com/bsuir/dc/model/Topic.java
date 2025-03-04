package com.bsuir.dc.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tbl_topic")
public class Topic {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne()
    @JoinColumn(name = "authorId", referencedColumnName = "id")
    private Author author;

    @Column(name = "title", unique = true)
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "created")
    private Date created;

    @Column(name = "modified")
    private Date modified;

    @ManyToMany(mappedBy = "topics", cascade = CascadeType.REMOVE)
    private List<Label> labels = new ArrayList<>();

    public void setId(long id) { this.id = id;}
    public long getId() { return id; }

    public void setCreator(Author author) { this.author = author; }
    public Author getAuthor() { return author; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setContent(String content) { this.content = content; }
    public String getContent() { return content; }

    public void setLabels(List<Label> labels) { this.labels = labels; }
    public List<Label> getLabels() { return labels; }

    public void setCreated(Date created) { this.created = created; }
    public Date getCreated() { return created; }

    public void setModified(Date modified) { this.modified = modified; }
    public Date getModified() { return modified; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return id == topic.id && author.equals(topic.author) && title.equals(topic.title) && content.equals(topic.content) && created.equals(topic.created) && modified.equals(topic.modified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, title, content, created, modified);
    }
}
