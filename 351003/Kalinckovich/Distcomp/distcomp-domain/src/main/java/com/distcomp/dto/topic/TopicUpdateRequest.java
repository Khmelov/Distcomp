package com.distcomp.dto.topic;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TopicUpdateRequest {

    @NotNull
    @Size(min = 2, max = 64)
    private String title;

    @NotNull
    @Size(min = 4, max = 2048)
    private String content;

    private Long userId;
}
