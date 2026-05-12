package by.shaminko.distcomp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class TweetResponseTo {
    long id;
    @JsonProperty("creatorId")
    long creatorId;
    @Size(min = 2, max = 64)
    String title;
    @Size(min = 4, max = 2048)
    String content;
    Timestamp created;
    Timestamp modified;
    @JsonProperty("markers")
    List<String> markers;
}
