package com.task.rest.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tbl_mark")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "tweets")
@NoArgsConstructor
@AllArgsConstructor
public class Mark extends BaseEntity {

    @Column(nullable = false, length = 32, unique = true)
    private String name;

    @ManyToMany(mappedBy = "marks", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Tweet> tweets;
}