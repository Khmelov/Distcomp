package org.polozkov.entity.label;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.polozkov.entity.issue.Issue;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tbl_label")
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "labels", fetch = FetchType.LAZY) // Указываем поле из класса Issue
    private List<Issue> issues;
}
