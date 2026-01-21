package com.example.publisher.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_editor")
public class Editor extends BaseEntity {

    @Column(name = "login", nullable = false, length = 64, unique = true)
    private String login;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "firstname", nullable = false, length = 64)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 64)
    private String lastname;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "editor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Story> stories = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }

    public List<Story> getStories() { return stories; }
    public void setStories(List<Story> stories) { this.stories = stories; }
}