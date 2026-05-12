package com.example.forum.service.impl;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import com.example.forum.entity.Post;
import com.example.forum.repository.PostRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
class PostServiceTest {

//    @Mock
//    private PostRepository repository;
//
//    @InjectMocks
//    private PostServiceImpl service; // Используем реальный класс, Mockito подставит в него Mock-репозиторий
//
//    @Test
//    void createPost_ShouldReturnResponse() {
//        // 1. Arrange
//        PostRequestTo request = new PostRequestTo();
//        request.setTopicId(102L);
//        request.setContent("Test content");
//
//        // Предполагаем, что Post имеет такой конструктор (проверь!)
//        Post savedPost = new Post(1L, 102L, "Test content");
//
//        when(repository.save(any(Post.class))).thenReturn(savedPost);
//
//        // 2. Act
//        PostResponseTo response = service.create(request);
//
//        // 3. Assert
//        assertNotNull(response);
//        assertEquals("Test content", response.getContent());
//        assertEquals(102L, response.getTopicId());
//
//        // Проверяем, что репозиторий реально дернулся
//        verify(repository, times(1)).save(any(Post.class));
//    }
}