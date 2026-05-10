package com.sergey.orsik.dto.request;

import com.sergey.orsik.config.StrictStringDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetRequestTo {

    private Long id;

    @NotNull(message = "creatorId must not be null")
    private Long creatorId;

    @NotBlank(message = "title must not be blank")
    @Size(min = 2, max = 64)
    private String title;

    @NotBlank(message = "content must not be blank")
    @Size(max = 2048)
    @JsonDeserialize(using = StrictStringDeserializer.class)
    private String content;

    private Instant created;
    private Instant modified;

    private Set<
            @NotBlank(message = "label name must not be blank")
            @Size(max = 32, message = "label name size must be at most 32")
            String> labels;
}
