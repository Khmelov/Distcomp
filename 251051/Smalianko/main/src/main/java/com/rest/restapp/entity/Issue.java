package com.rest.restapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "issues")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Issue {

    @Id
    @GeneratedValue
    Long id;

    @NotNull(message = "Author is required")
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", table = "authors")
    Author author;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 64, message = "Title length is not valid")
    String title;

    @NotBlank(message = "Content is required")
    @Size(max = 2048, message = "Content must not exceed 2048 characters")
    String content;

    @CreationTimestamp
    OffsetDateTime created;

    @UpdateTimestamp
    OffsetDateTime modified;
}