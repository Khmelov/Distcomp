package com.sergey.orsik.discussion.repository;

import com.sergey.orsik.discussion.cassandra.CommentByTweetKey;
import com.sergey.orsik.discussion.cassandra.CommentByTweetRow;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface CommentByTweetRepository extends CassandraRepository<CommentByTweetRow, CommentByTweetKey> {

    List<CommentByTweetRow> findByKeyTweetId(Long tweetId);
}
