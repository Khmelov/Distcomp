package com.messageservice.services;

import com.messageservice.configs.cassandraconfig.DiscussionCassandraProperties;
import com.messageservice.configs.exceptionhandlerconfig.exceptions.MessageNotFoundException;
import com.messageservice.configs.exceptionhandlerconfig.exceptions.TweetNotFoundException;
import com.messageservice.configs.tweetclientconfig.TweetClient;
import com.messageservice.dtos.MessageRequestTo;
import com.messageservice.dtos.MessageResponseTo;
import com.messageservice.models.Message;
import com.messageservice.repositories.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTests {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private TweetClient tweetClient;

    @Mock
    private DiscussionCassandraProperties cassandraProperties;

    @InjectMocks
    private MessageService messageService;

    @Test
    void createMessageStoresMessageInCassandraShape() {
        MessageRequestTo request = new MessageRequestTo();
        request.setTweetId(15L);
        request.setContent("content");

        when(cassandraProperties.getBucketCount()).thenReturn(8);
        when(tweetClient.getTweetById(15L)).thenReturn(Map.of("id", 15L));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MessageResponseTo response = messageService.createMessage(request);

        assertNotNull(response.getId());
        assertEquals(15L, response.getTweetId());
        assertEquals("content", response.getContent());

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());
        Message saved = captor.getValue();
        assertEquals(15L, saved.getTweetId());
        assertEquals("content", saved.getContent());
        assertNotNull(saved.getBucket());
    }

    @Test
    void createMessageRejectsUnknownTweet() {
        MessageRequestTo request = new MessageRequestTo();
        request.setTweetId(99L);
        request.setContent("content");

        doThrow(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        )).when(tweetClient).getTweetById(99L);

        assertThrows(TweetNotFoundException.class, () -> messageService.createMessage(request));
    }

    @Test
    void updateMessageByIdReusesOriginalTweetAndBucket() {
        MessageRequestTo request = new MessageRequestTo();
        request.setTweetId(77L);
        request.setContent("updated");

        Message existing = Message.builder()
                .id(10L)
                .tweetId(15L)
                .bucket(3)
                .content("old")
                .build();

        when(messageRepository.findMessageById(10L)).thenReturn(Optional.of(existing));
        when(messageRepository.save(existing)).thenReturn(existing);

        MessageResponseTo response = messageService.updateMessageById(request, 10L);

        assertEquals(10L, response.getId());
        assertEquals(15L, response.getTweetId());
        assertEquals("updated", response.getContent());
    }

    @Test
    void deleteMessageByTweetIdDelegatesToRepository() {
        messageService.deleteMessageByTweetId(15L);
        verify(messageRepository).deleteAllByTweetId(15L);
    }

    @Test
    void findMessageByIdThrowsWhenMissing() {
        when(messageRepository.findMessageById(eq(123L))).thenReturn(Optional.empty());
        assertThrows(MessageNotFoundException.class, () -> messageService.findMessageById(123L));
    }

    @Test
    void findMessagesByTweetIdMapsRepositoryResults() {
        when(messageRepository.findAllByTweetId(15L)).thenReturn(List.of(
                Message.builder().id(1L).tweetId(15L).bucket(0).content("one").build(),
                Message.builder().id(2L).tweetId(15L).bucket(1).content("two").build()
        ));

        List<MessageResponseTo> response = messageService.findMessagesByTweetId(15L);

        assertEquals(2, response.size());
        assertEquals("one", response.getFirst().getContent());
        assertEquals("two", response.getLast().getContent());
    }
}
