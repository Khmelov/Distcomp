package com.lab1.lab1DC.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_notice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notice {
    @Id
    private Long id;

    @Column(nullable = false, length = 2048)
    private String content;

    @Column(name = "storyId", nullable = false)
    private Long storyId;

}

