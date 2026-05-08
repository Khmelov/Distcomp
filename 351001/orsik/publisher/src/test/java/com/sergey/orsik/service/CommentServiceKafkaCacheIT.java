package com.sergey.orsik.service;

import com.sergey.orsik.dto.kafka.CommentTransportRequest;
import com.sergey.orsik.entity.Tweet;
import com.sergey.orsik.kafka.DiscussionStubKafkaConfig;
import com.sergey.orsik.repository.TweetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"InTopic", "OutTopic"})
@Import(DiscussionStubKafkaConfig.class)
class CommentServiceKafkaCacheIT {

    @Autowired
    private CommentService commentService;

    @SpyBean
    private KafkaTemplate<String, CommentTransportRequest> kafkaTemplate;

    @MockBean
    private TweetRepository tweetRepository;

    @BeforeEach
    void stubTweetForPotentialCreates() {
        Tweet tweet = new Tweet();
        tweet.setId(2L);
        tweet.setCreatorId(1L);
        tweet.setTitle("t");
        tweet.setContent("c");
        tweet.setLabels(new HashSet<>());
        when(tweetRepository.findById(org.mockito.ArgumentMatchers.anyLong())).thenReturn(Optional.of(tweet));
    }

    @Test
    void findByIdSecondCallDoesNotSendKafkaRequest() {
        assertThat(commentService.findById(1L).getId()).isEqualTo(1L);
        assertThat(commentService.findById(1L).getId()).isEqualTo(1L);

        verify(kafkaTemplate, times(1)).send(eq("InTopic"), anyString(), any(CommentTransportRequest.class));
    }
}
