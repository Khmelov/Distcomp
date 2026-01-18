package com.rest.restapp.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Table(name = "notices")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notice {

    @Id
    @GeneratedValue
    Long id;

    @NotNull(message = "Issue is required")
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", table = "issue")
    Issue issue;

    @NotBlank(message = "Content is required")
    @Size(min = 4, max = 2048, message = "Content (4...2048 chars)")
    String content;
}