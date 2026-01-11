package com.distcomp.discussion;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.distcomp.discussion.post.repo.PostRepository;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration"
})
class DiscussionApplicationTests {

    @MockBean
    private PostRepository postRepository;

    @Test
    void contextLoads() {
    }
}
