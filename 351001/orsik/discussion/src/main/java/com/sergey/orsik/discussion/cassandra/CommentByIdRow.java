package com.sergey.orsik.discussion.cassandra;

import com.sergey.orsik.dto.CommentState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("tbl_comment_by_id")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentByIdRow {

    @PrimaryKey
    private Long id;

    @Column("tweet_id")
    private Long tweetId;

    @Column("creator_id")
    private Long creatorId;

    @Column
    private String content;

    @Column
    private Instant created;

    @Column
    private CommentState state;
}
