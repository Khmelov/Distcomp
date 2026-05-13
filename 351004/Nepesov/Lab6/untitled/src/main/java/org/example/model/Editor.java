package org.example.model;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_editor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Editor {

    @PrimaryKey
    private Long id;

    // Добавляем индекс, чтобы поиск existsByLogin работал без ошибок 500
    @Indexed
    private String login;

    private String password;

    private String firstname;

    private String lastname;
}