package by.kapinskiy.Task310.DTOs.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class TagRequestDTO {

    private Long id;

 //   @NotNull(message = "Issue id can't be null")
    private Long issueId;

    @NotBlank(message = "Name may not be blank")
    @Size(min = 2, max = 32, message = "Name should be between 2 and 32 symbols")
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
