package com.distcomp.dto.note;

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
public class NoteUpdateRequest {

    @NotNull
    @Size(min = 2, max = 2048)
    private String content;

    @NotNull
    private Long topicId;
}