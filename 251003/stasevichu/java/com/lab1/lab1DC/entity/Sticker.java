package com.lab1.lab1DC.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "tbl_sticker")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Sticker {
    @Id
    @Column
    private Long id;
    @Column
    private String name;
}
