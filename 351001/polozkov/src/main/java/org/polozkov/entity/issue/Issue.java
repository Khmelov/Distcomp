package org.polozkov.entity.issue;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.polozkov.entity.comment.Comment;
import org.polozkov.entity.label.Label;
import org.polozkov.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private LocalDateTime created;

    private LocalDateTime modified;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "issue", fetch = FetchType.LAZY)
    private List<Comment> comments;

    @ManyToMany(mappedBy = "issues", fetch = FetchType.LAZY)
    private List<Label> labels;

}
