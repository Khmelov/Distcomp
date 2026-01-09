package org.example.task340.publisher.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_label", schema = "distcomp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Label extends BaseEntity {

    @Column(nullable = false, length = 32, unique = true)
    @Size(min = 2, max = 32)
    private String name;

    @OneToMany(mappedBy = "label", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TweetLabel> tweetLabels = new HashSet<>();
}

