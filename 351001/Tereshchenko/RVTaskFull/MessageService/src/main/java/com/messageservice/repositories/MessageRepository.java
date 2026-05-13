package com.messageservice.repositories;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.messageservice.configs.cassandraconfig.DiscussionCassandraProperties;
import com.messageservice.models.Message;
import com.messageservice.models.MessageState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final CqlSession session;
    private final DiscussionCassandraProperties properties;

    public Message save(Message message) {
        session.execute(SimpleStatement.newInstance(
                "INSERT INTO %s.tbl_message (id, tweet_id, bucket, content, state) VALUES (?, ?, ?, ?, ?)"
                        .formatted(properties.getKeyspace()),
                message.getId(),
                message.getTweetId(),
                message.getBucket(),
                message.getContent(),
                message.getState().name()
        ));

        session.execute(SimpleStatement.newInstance(
                "INSERT INTO %s.tbl_message_by_tweet (tweet_id, bucket, id, content, state) VALUES (?, ?, ?, ?, ?)"
                        .formatted(properties.getKeyspace()),
                message.getTweetId(),
                message.getBucket(),
                message.getId(),
                message.getContent(),
                message.getState().name()
        ));

        return message;
    }

    public List<Message> findAll() {
        ResultSet resultSet = session.execute(SimpleStatement.newInstance(
                "SELECT id, tweet_id, bucket, content, state FROM %s.tbl_message"
                        .formatted(properties.getKeyspace())
        ));

        List<Message> messages = new ArrayList<>();
        for (Row row : resultSet) {
            messages.add(toMessage(row));
        }

        messages.sort(Comparator.comparing(Message::getId));
        return messages;
    }

    public Optional<Message> findMessageById(Long id) {
        Row row = session.execute(SimpleStatement.newInstance(
                "SELECT id, tweet_id, bucket, content, state FROM %s.tbl_message WHERE id = ?"
                        .formatted(properties.getKeyspace()),
                id
        )).one();

        return Optional.ofNullable(row).map(this::toMessage);
    }

    public List<Message> findAllByTweetId(Long tweetId) {
        List<Message> messages = new ArrayList<>();

        for (int bucket = 0; bucket < properties.getBucketCount(); bucket++) {
            ResultSet resultSet = session.execute(SimpleStatement.newInstance(
                    "SELECT tweet_id, bucket, id, content, state FROM %s.tbl_message_by_tweet WHERE tweet_id = ? AND bucket = ?"
                            .formatted(properties.getKeyspace()),
                    tweetId,
                    bucket
            ));

            for (Row row : resultSet) {
                messages.add(Message.builder()
                        .id(row.getLong("id"))
                        .tweetId(row.getLong("tweet_id"))
                        .bucket(row.getInt("bucket"))
                        .content(row.getString("content"))
                        .state(toState(row.getString("state")))
                        .build());
            }
        }

        messages.sort(Comparator.comparing(Message::getId));
        return messages;
    }

    public boolean existsById(Long id) {
        return findMessageById(id).isPresent();
    }

    public void delete(Message message) {
        session.execute(SimpleStatement.newInstance(
                "DELETE FROM %s.tbl_message WHERE id = ?"
                        .formatted(properties.getKeyspace()),
                message.getId()
        ));

        session.execute(SimpleStatement.newInstance(
                "DELETE FROM %s.tbl_message_by_tweet WHERE tweet_id = ? AND bucket = ? AND id = ?"
                        .formatted(properties.getKeyspace()),
                message.getTweetId(),
                message.getBucket(),
                message.getId()
        ));
    }

    public void deleteAllByTweetId(Long tweetId) {
        List<Message> messages = findAllByTweetId(tweetId);
        for (Message message : messages) {
            delete(message);
        }
    }

    private Message toMessage(Row row) {
        return Message.builder()
                .id(row.getLong("id"))
                .tweetId(row.getLong("tweet_id"))
                .bucket(row.getInt("bucket"))
                .content(row.getString("content"))
                .state(toState(row.getString("state")))
                .build();
    }

    private MessageState toState(String state) {
        return state == null ? MessageState.APPROVE : MessageState.valueOf(state);
    }
}
