package by.bsuir.distcomp.support;

import by.bsuir.distcomp.client.DiscussionReactionClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DiscussionTestSupport {

    @Bean
    public DiscussionReactionClient discussionReactionClient() {
        return new InMemoryDiscussionReactionClient();
    }
}
