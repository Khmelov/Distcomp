package com.lizaveta.notebook.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
        ObjectMapper discussionPayloadMapper = objectMapper.copy();
        discussionPayloadMapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
        discussionPayloadMapper.disable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        MappingJackson2HttpMessageConverter jsonConverter =
                new MappingJackson2HttpMessageConverter(discussionPayloadMapper);
        return RestClient.builder()
                .baseUrl(discussionBaseUrl)
                .messageConverters(converters -> converters.add(jsonConverter))
                .build();
    }
}
