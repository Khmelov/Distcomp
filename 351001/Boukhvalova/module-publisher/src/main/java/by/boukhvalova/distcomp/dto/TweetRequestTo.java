package by.boukhvalova.distcomp.dto;

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
    @JsonProperty("userId")
    @JsonAlias("creatorId")
    @Positive
    long userId;
    @NotBlank
    @Size(min = 2, max = 64)
    String title;
    @NotBlank
    @Size(min = 4, max = 2048)
    String content;
    @JsonProperty("stickers")
    @JsonAlias({"markers", "tags"})
    private List<String> stickers;
}
