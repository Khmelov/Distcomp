package by.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class StoryRequestTo {
    @NotNull 
    private Long editorId;
    
    @NotBlank @Size(min = 2, max = 64) 
    private String title;
    
    @NotBlank @Size(min = 4, max = 2048) 
    private String content;
    
    private Set<Long> tagIds;

    // Getters and Setters
    public Long getEditorId() { return editorId; }
    public void setEditorId(Long editorId) { this.editorId = editorId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Set<Long> getTagIds() { return tagIds; }
    public void setTagIds(Set<Long> tagIds) { this.tagIds = tagIds; }
}