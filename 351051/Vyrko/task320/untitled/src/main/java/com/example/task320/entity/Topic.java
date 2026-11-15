package com.example.task320.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

// Сущность темы, соответствующая таблице tbl_topic в базе данных
@Entity
@Table(name = "tbl_topic")
public class Topic {

    // Уникальный идентификатор темы, генерируемый базой данных
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Редактор, создавший тему; связь многие к одному
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editor_id", nullable = false)
    private Editor editor;

    // Заголовок темы
    @Column(nullable = false)
    private String title;

    // Основное содержимое темы
    @Column(columnDefinition = "TEXT")
    private String content;

    // Дата и время создания темы
    private LocalDateTime created;

    // Дата и время последнего изменения темы
    private LocalDateTime modified;

    // При удалении темы удаляются все связанные с ней реакции.
    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
    private List<Reaction> reactions;

    // Список маркеров, связанных с темой
    @ManyToMany
    @JoinTable(
            name = "tbl_topic_marker",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id")
    )
    private List<Marker> markers;

    // Конструктор без параметров, необходимый для JPA
    public Topic() {
    }

    // Конструктор для создания темы с основными данными
    public Topic(Editor editor, String title, String content) {
        this.editor = editor;
        this.title = title;
        this.content = content;
    }

    // Возвращает идентификатор темы
    public Long getId() {
        return id;
    }

    // Устанавливает идентификатор темы
    public void setId(Long id) {
        this.id = id;
    }

    // Возвращает редактора темы
    public Editor getEditor() {
        return editor;
    }

    // Устанавливает редактора темы
    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    // Возвращает заголовок темы
    public String getTitle() {
        return title;
    }

    // Устанавливает заголовок темы
    public void setTitle(String title) {
        this.title = title;
    }

    // Возвращает содержимое темы
    public String getContent() {
        return content;
    }

    // Устанавливает содержимое темы
    public void setContent(String content) {
        this.content = content;
    }

    // Возвращает дату создания темы
    public LocalDateTime getCreated() {
        return created;
    }

    // Устанавливает дату создания темы
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    // Возвращает дату последнего изменения темы
    public LocalDateTime getModified() {
        return modified;
    }

    // Устанавливает дату последнего изменения темы
    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    // Возвращает список реакций темы
    public List<Reaction> getReactions() {
        return reactions;
    }

    // Устанавливает список реакций темы
    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    // Возвращает список маркеров темы
    public List<Marker> getMarkers() {
        return markers;
    }

    // Устанавливает список маркеров темы
    public void setMarkers(List<Marker> markers) {
        this.markers = markers;
    }
}
