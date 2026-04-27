package by.bsuir.distcomp.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(name = "discussion.transport", havingValue = "rest")
public class DiscussionClientConfig {

    /**
     * Обязательно использовать {@link RestClient.Builder} из Spring Boot — иначе нет Jackson-конвертеров
     * и {@code body(ParameterizedTypeReference<List<...>>)} падает с 500 на publisher.
     */
    @Bean
    @Qualifier("discussionRestClient")
    public RestClient discussionRestClient(
            RestClient.Builder restClientBuilder,
            @Value("${discussion.base-url:http://localhost:24130}") String baseUrl) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofSeconds(120));
        return restClientBuilder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
