package by.bsuir.rest.support;

import by.bsuir.rest.client.DiscussionClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MemoryDiscussionClientConfiguration {

    @Bean
    public DiscussionClient discussionClient() {
        return new MemoryDiscussionClient();
    }
}
