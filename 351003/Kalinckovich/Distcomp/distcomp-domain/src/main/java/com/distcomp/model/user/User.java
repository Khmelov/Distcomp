package com.distcomp.model.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.Size;
import lombok.*;

@Table(name = "tbl_user", schema = "distcomp")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private Long id;

    @Size(min = 2, max = 64)
    private String login;

    @Size(min = 2, max = 64)
    @Column("firstname")
    private String firstname;

    @Size(min = 2, max = 64)
    @Column("lastname")
    private String lastname;

    @Size(min = 8, max = 128)
    private String password;
}