package com.example.restservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tbl_news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive
    Long id;

    @ManyToOne
    @JoinColumn(name = "authorId", nullable = false)
    Author author;

    @Size(min=2, max=64)
    String title;

    @Size(min=4, max=2048)
    String content;

    Date created;
    Date modified;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "news_marker",
            joinColumns = @JoinColumn(name = "newsId"),
            inverseJoinColumns = @JoinColumn(name = "markerId")
    )
    private List<Marker> markers;
    @PreRemove
    private void preRemove() {
        // Создаем копию списка для безопасного удаления
        new ArrayList<>(markers).forEach(this::removeMarker);
    }

    public void removeMarker(Marker marker) {
        markers.remove(marker);
        marker.getNewsList().remove(this);
    }
}
