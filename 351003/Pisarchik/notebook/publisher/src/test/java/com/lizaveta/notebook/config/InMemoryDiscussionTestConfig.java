package com.lizaveta.notebook.config;

import com.lizaveta.notebook.client.DiscussionNoticeClient;
import com.lizaveta.notebook.client.InMemoryDiscussionNoticeClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class InMemoryDiscussionTestConfig {

    @Bean
    @Primary
    public DiscussionNoticeClient discussionNoticeClient() {
        return new InMemoryDiscussionNoticeClient();
    }
}
