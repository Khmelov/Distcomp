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
    @Size(max = 2048, message = "Content must not exceed 2048 characters")
    String content;
}