package com.rest.restapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "tbl_issue")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull(message = "User is required")
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 64, message = "Title length is not valid")
    String title;

    @NotBlank(message = "Content is required")
    @Size(min = 4, max = 2048, message = "Content length is not valid")
    String content;

    @ManyToMany
    @JoinTable(
            name = "tbl_issue_tag",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    List<Tag> tags;

    @CreationTimestamp
    OffsetDateTime created;

    @UpdateTimestamp
    OffsetDateTime modified;
}