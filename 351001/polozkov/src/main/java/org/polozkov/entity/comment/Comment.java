package org.polozkov.entity.comment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.polozkov.entity.issue.Issue;

@Entity
@Getter
@Setter
public class Comment {

    @Id
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

}
