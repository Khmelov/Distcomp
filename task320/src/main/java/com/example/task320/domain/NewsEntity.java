package com.example.task320.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "tbl_news")
public class NewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "writer_id", nullable = false)
    private WriterEntity writer;

    @Column(nullable = false, length = 64, unique = true)
    private String title;

    @Column(nullable = false, length = 2048)
    private String content;

    @Column(nullable = false)
    private OffsetDateTime created;

    @Column(nullable = false)
    private OffsetDateTime modified;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeEntity> notices = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tbl_news_sticker",
            joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "sticker_id")
    )
    private Set<StickerEntity> stickers = new HashSet<>();

    public Long getId() { return id; }
    public WriterEntity getWriter() { return writer; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public OffsetDateTime getCreated() { return created; }
    public OffsetDateTime getModified() { return modified; }
    public Set<StickerEntity> getStickers() { return stickers; }

    public void setId(Long id) { this.id = id; }
    public void setWriter(WriterEntity writer) { this.writer = writer; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setCreated(OffsetDateTime created) { this.created = created; }
    public void setModified(OffsetDateTime modified) { this.modified = modified; }
}
