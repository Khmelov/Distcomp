package com.sergey.orsik.discussion.cassandra;

import com.sergey.orsik.dto.CommentState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_comment_by_tweet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentByTweetRow {

    @PrimaryKey
    private CommentByTweetKey key;

    @Column("creator_id")
    private Long creatorId;

    @Column
    private String content;

    @Column
    private CommentState state;
}
