package by.molchan.Publisher.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;


public class ArticleRequestDTO {
    private Long id;

    @NotNull(message = "Creator id can't be null")
    private Long creatorId;

    @NotBlank(message = "Title may not be blank")
    @Size(min = 2, max = 64, message = "Title should be between 2 and 64 symbols")
    private String title;

    @NotBlank(message = "Content may not be blank")
    @Size(min = 4, max = 2048, message = "Content should be between 4 and 2048 symbols")
    private String content;

    private List<String> labels = new ArrayList<>();

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
