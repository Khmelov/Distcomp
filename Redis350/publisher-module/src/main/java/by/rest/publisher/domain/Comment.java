package by.rest.publisher.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tbl_comment")
public class Comment {
    
    @Id
    private UUID id;
    
    @Column(name = "story_id", nullable = false)
    private Long storyId;
    
    @Column(name = "content", nullable = false, length = 2048)
    private String content;
    
    @Column(name = "author", length = 100)
    private String author;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status; // PENDING, APPROVE, DECLINE
    
    @Column(name = "created_date")
    private Instant createdDate;
    
    @Column(name = "modified_date")
    private Instant modifiedDate;
    
    // Для JPA
    public Comment() {
        this.createdDate = Instant.now();
        this.modifiedDate = Instant.now();
        this.author = "anonymous";
    }
    
    public Comment(UUID id, String content, Long storyId) {
        this();
        this.id = id;
        this.content = content;
        this.storyId = storyId;
        this.status = "PENDING";
    }
    
    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { 
        this.id = id;
        this.modifiedDate = Instant.now();
    }
    
    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { 
        this.storyId = storyId;
        this.modifiedDate = Instant.now();
    }
    
    public String getContent() { return content; }
    public void setContent(String content) { 
        this.content = content;
        this.modifiedDate = Instant.now();
    }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { 
        this.author = author;
        this.modifiedDate = Instant.now();
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status;
        this.modifiedDate = Instant.now();
    }
    
    public Instant getCreatedDate() { return createdDate; }
    public void setCreatedDate(Instant createdDate) { this.createdDate = createdDate; }
    
    public Instant getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(Instant modifiedDate) { this.modifiedDate = modifiedDate; }
    
    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = Instant.now();
    }
    
    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", storyId=" + storyId +
                ", author='" + author + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}