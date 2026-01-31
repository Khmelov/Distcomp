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
    private Long id;

    private String text;

    private String content;

    private LocalDateTime created;

    private LocalDateTime modified;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "issue")
    private List<Comment> comments;

    @ManyToMany(mappedBy = "issues")
    private List<Label> labels;

}
