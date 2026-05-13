package org.example.model;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_sticker")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sticker {

    @PrimaryKey
    private Long id;

    private String name;
}