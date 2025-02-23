package by.kapinskiy.Distcomp.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class NoteRequestDTO {


    private Long id;

    @NotNull(message = "Issue id can't be null")
    private Long issueId;

    @NotBlank(message = "Content may not be blank")
    @Size(min = 2, max = 2048, message = "Content should be between 2 and 2048 symbols")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
