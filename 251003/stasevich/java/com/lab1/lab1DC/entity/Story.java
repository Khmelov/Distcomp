package com.lab1.lab1DC.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tbl_story")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Story {
    @Id
    @Column
    private Long id;
    @Column
    private String title;
    @Column
    private String content;
    @Column
    private Long userId;
    @Column
    private List<UUID> stickersIds;
    @Column
    private List<UUID> noticeIds;
}