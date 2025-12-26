package com.restApp.service.impl;

import com.restApp.client.DiscussionClient;
import com.restApp.dto.CommentResponseTo;
import com.restApp.dto.NewsRequestTo;
import com.restApp.dto.NewsResponseTo;
import com.restApp.mapper.NewsMapper;
import com.restApp.model.Author;
import com.restApp.model.News;
import com.restApp.repository.AuthorRepository;
import com.restApp.repository.MarkRepository;
import com.restApp.repository.NewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @Mock
    private NewsRepository newsRepository;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private MarkRepository markRepository;
    @Mock
    private NewsMapper newsMapper;
    @Mock
    private DiscussionClient discussionClient;

    @InjectMocks
    private NewsServiceImpl newsService;

    private News news;
    private NewsResponseTo responseTo;

    @BeforeEach
    void setUp() {
        news = new News();
        news.setId(1L);
        news.setTitle("Title");

        responseTo = new NewsResponseTo();
        responseTo.setId(1L);
        responseTo.setTitle("Title");
    }

    @Test
    void findById_shouldReturnNewsWithComments() {
        // Given
        given(newsRepository.findById(1L)).willReturn(Optional.of(news));
        given(newsMapper.toResponse(news)).willReturn(responseTo);

        CommentResponseTo comment = new CommentResponseTo(10L, 1L, "Comment Content", "US");
        given(discussionClient.getCommentsByNewsId(1L)).willReturn(List.of(comment));

        // When
        NewsResponseTo result = newsService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).content()).isEqualTo("Comment Content");

        verify(discussionClient).getCommentsByNewsId(1L);
    }
}
