package com.homel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeMethodRequestTo {
    private String method;

    private Long id;

    private String state;

    @NotNull
    private Long storyId;

    @NotBlank
    @Size(min = 2, max = 2048)
    private String content;
}
