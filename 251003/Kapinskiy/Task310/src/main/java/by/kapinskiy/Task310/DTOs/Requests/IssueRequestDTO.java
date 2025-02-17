package by.kapinskiy.Task310.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class IssueRequestDTO {


    private Long id;

    @NotNull(message = "User id can't be null")
    private Long userId;

    @NotBlank(message = "Title may not be blank")
    @Size(min = 2, max = 64, message = "Title should be between 2 and 64 symbols")
    private String title;

    @NotBlank(message = "Content may not be blank")
    @Size(min = 4, max = 2048, message = "Content should be between 4 and 2048 symbols")
    private String content;
/*    private Date created;
    private Date modified;*/

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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
