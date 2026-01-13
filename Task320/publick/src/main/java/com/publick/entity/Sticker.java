package com.publick.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "tbl_sticker", schema = "distcomp")
public class Sticker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 32)
    @Column(nullable = false, length = 32)
    private String name;

    @OneToMany(mappedBy = "sticker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IssueSticker> issueStickers;

    public Sticker() {
    }

    public Sticker(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IssueSticker> getIssueStickers() {
        return issueStickers;
    }

    public void setIssueStickers(List<IssueSticker> issueStickers) {
        this.issueStickers = issueStickers;
    }
}