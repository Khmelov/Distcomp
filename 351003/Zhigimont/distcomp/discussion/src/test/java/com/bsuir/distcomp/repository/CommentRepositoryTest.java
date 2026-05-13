package com.bsuir.distcomp.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository repository;

    @Test
    void findByTopic_shouldReturnList() {
        var list = repository.findByKeyTopicId(1L);
        assertNotNull(list);
    }
}
