package com.tweetmarkersservice.configs.markerclientconfig;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class MarkerClientConfig {

    @Bean
    public MarkerClient markerClient(@LoadBalanced RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl("http://markerservice")
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(MarkerClient.class);
    }
}
