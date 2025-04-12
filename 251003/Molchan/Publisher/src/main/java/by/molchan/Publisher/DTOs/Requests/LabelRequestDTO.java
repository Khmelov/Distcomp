package by.molchan.Publisher.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class LabelRequestDTO {

    private Long id;

 //   @NotNull(message = "Article id can't be null")
    private Long articleId;

    @NotBlank(message = "Name may not be blank")
    @Size(min = 2, max = 32, message = "Name should be between 2 and 32 symbols")
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
