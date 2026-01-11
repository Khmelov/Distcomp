package by.rest.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_comment")
public class Comment {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Story getStory() { return story; }
    public void setStory(Story story) { this.story = story; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}