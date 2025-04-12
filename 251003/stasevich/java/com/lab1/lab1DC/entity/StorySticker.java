package com.lab1.lab1DC.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "tbl_story_sticker")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class StorySticker {
    @Id
    @Column
    private Long id;
    @Column
    private Long story_id;
    @Column
    private Long sticker_id;
    public StorySticker(Long id, Long story_id, Long sticker_id){
        this.id = id;
        this.story_id = story_id;
        this.sticker_id = sticker_id;
    }
}
