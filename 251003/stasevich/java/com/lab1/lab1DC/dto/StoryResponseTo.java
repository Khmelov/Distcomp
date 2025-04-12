package com.lab1.lab1DC.dto;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StoryResponseTo {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private List<UUID> stickerIds;
    private List<UUID> noticeIds;
}