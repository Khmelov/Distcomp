package by.shaminko.distcomp.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TweetRequestTo {
    long id;
    @JsonProperty("creatorId")
    @JsonAlias("userId")
    @Positive
    long creatorId;
    @NotBlank
    @Size(min = 2, max = 64)
    String title;
    @NotBlank
    @Size(min = 4, max = 2048)
    String content;
    @JsonProperty("markers")
    @JsonAlias("tags")
    private List<String> markers;
}
