package com.sergey.orsik.service;

import com.sergey.orsik.dto.CommentState;
import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import com.sergey.orsik.entity.Tweet;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.kafka.DiscussionStubKafkaConfig;
import com.sergey.orsik.repository.TweetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"InTopic", "OutTopic"})
@Import(DiscussionStubKafkaConfig.class)
class CommentServiceKafkaImplIT {

    @Autowired
    private CommentService commentService;

    @MockBean
    private TweetRepository tweetRepository;

    @Test
    void createReturnsPendingWithoutWaitingForOutTopic() {
        Tweet tweet = new Tweet();
        tweet.setId(2L);
        tweet.setCreatorId(1L);
        tweet.setTitle("t");
        tweet.setContent("c");
        tweet.setLabels(new HashSet<>());
        when(tweetRepository.findById(2L)).thenReturn(Optional.of(tweet));
        when(tweetRepository.findById(anyLong())).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            return id.equals(2L) ? Optional.of(tweet) : Optional.empty();
        });

        CommentResponseTo created = commentService.create(new CommentRequestTo(null, 2L, "hello", null));

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTweetId()).isEqualTo(2L);
        assertThat(created.getState()).isEqualTo(CommentState.PENDING);
    }

    @Test
    void findByIdUsesKafkaRpc() {
        CommentResponseTo found = commentService.findById(1L);
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getTweetId()).isEqualTo(2L);
        assertThat(found.getState()).isEqualTo(CommentState.APPROVE);
    }

    @Test
    void findByIdMapsErrorReplyToNotFound() {
        assertThatThrownBy(() -> commentService.findById(404L)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findAllUsesKafkaRpc() {
        List<CommentResponseTo> list = commentService.findAll(0, 10, "id", "asc", 9L, null);
        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getTweetId()).isEqualTo(9L);
    }
}
