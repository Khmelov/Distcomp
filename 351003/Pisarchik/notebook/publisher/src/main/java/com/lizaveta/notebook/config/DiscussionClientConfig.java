package com.lizaveta.notebook.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class DiscussionClientConfig {

    @Bean
    @org.springframework.beans.factory.annotation.Qualifier("discussion")
    public RestClient discussionRestClient(
            @Value("${discussion.base-url}") final String discussionBaseUrl,
            final ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        return RestClient.builder()
                .baseUrl(discussionBaseUrl)
                .messageConverters(converters -> converters.add(jsonConverter))
                .build();
    }
}
