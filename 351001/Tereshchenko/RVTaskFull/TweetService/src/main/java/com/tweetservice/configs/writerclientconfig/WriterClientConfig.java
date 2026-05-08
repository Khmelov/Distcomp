package com.tweetservice.configs.writerclientconfig;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WriterClientConfig {

    @Bean
    public WriterClient writerClient(@LoadBalanced RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl("http://writerservice")
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(WriterClient.class);
    }
}
