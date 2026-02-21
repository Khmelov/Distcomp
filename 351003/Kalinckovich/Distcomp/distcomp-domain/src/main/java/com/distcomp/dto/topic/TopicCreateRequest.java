package com.distcomp.dto.topic;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TopicCreateRequest{

    @NotNull
    @Size(min = 2, max = 64)
    private String title;

    @NotNull
    @Size(min = 4, max = 2048)
    private String content;

    @Size(min = 2, max = 64)
    private List<String> tags;

    @NotNull
    @Positive
    private Long userId;

}
