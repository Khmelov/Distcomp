package com.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_topic", schema = "distcomp")
public class Topic extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id", nullable = false)
    private Editor editor;

    @Column(name = "title", nullable = false, length = 64)
    @Size(min = 2, max = 64)
    private String title;

    @Column(name = "content", nullable = false, length = 2048)
    @Size(min = 4, max = 2048)
    private String content;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_topic_tag",
            schema = "distcomp",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // Конструкторы
    public Topic() {
        super();
    }

    // Вспомогательные методы для работы с тегами
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getTopics().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getTopics().remove(this);
    }

    // Геттеры и сеттеры
    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
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

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

}