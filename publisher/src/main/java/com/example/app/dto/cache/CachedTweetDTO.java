package com.example.app.dto.cache;

import com.example.app.dto.ReactionResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CachedTweetDTO implements Serializable {
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private Instant created;
    private Instant modified;
    private List<ReactionResponseDTO> reactions;
    private List<Long> tagIds;
}