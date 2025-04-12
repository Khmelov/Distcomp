package com.lab1.lab1DC.dto;

import com.lab1.lab1DC.entity.Sticker;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StoryRequestTo {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private List<String> stickers;
    private List<UUID> stickerIds;
}