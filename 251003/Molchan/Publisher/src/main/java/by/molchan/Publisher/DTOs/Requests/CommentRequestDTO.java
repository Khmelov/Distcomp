package by.molchan.Publisher.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class CommentRequestDTO {


    private Long id;

    @NotNull(message = "Article id can't be null")
    private Long articleId;

    @NotBlank(message = "Content may not be blank")
    @Size(min = 2, max = 2048, message = "Content should be between 2 and 2048 symbols")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
