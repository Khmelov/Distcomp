package by.boukhvalova.distcomp.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class TweetResponseTo {
    long id;
    @JsonProperty("userId")
    @JsonAlias("creatorId")
    long userId;
    @Size(min = 2, max = 64)
    String title;
    @Size(min = 4, max = 2048)
    String content;
    Timestamp created;
    Timestamp modified;
    @JsonProperty("stickers")
    @JsonAlias({"markers", "tags"})
    List<String> stickers;
}
