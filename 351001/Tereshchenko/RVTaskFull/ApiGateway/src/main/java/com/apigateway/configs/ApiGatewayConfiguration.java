package com.apigateway.configs;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RedisRateLimiter rateLimiter() {
        return new RedisRateLimiter(10, 20, 10);
    }

    @Bean
    public KeyResolver keyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }

    @Bean
    public RouteLocator gatewayConfigRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("writerServiceV2", r -> r
                        .path("/api/v2.0/writers/**")
                        .filters(f -> f.rewritePath("/api/v2.0/(?<segment>.*)", "/api/v1.0/${segment}"))
                        .uri("lb://WRITERSERVICE"))
                .route("tweetServiceV2", r -> r
                        .path("/api/v2.0/tweets/**")
                        .filters(f -> f.rewritePath("/api/v2.0/(?<segment>.*)", "/api/v1.0/${segment}"))
                        .uri("lb://tweetService"))
                .route("messageServiceV2", r -> r
                        .path("/api/v2.0/messages/**")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f -> f.rewritePath("/api/v2.0/(?<segment>.*)", "/api/v1.0/${segment}"))
                        .uri("lb://messageService"))
                .route("markerServiceV2", r -> r
                        .path("/api/v2.0/markers/**")
                        .filters(f -> f.rewritePath("/api/v2.0/(?<segment>.*)", "/api/v1.0/${segment}"))
                        .uri("lb://markerService"))
                .route("tweetMarkersServiceV2", r -> r
                        .path("/api/v2.0/tweet-markers/**")
                        .filters(f -> f.rewritePath("/api/v2.0/(?<segment>.*)", "/api/v1.0/${segment}"))
                        .uri("lb://tweetMarkersService"))
                .route("writerService", r -> r
                        .path("/api/v1.0/writers/**")
//                        .filters(f -> f
//                                .requestRateLimiter(config -> config
//                                        .setRateLimiter(rateLimiter())
//                                        .setKeyResolver(keyResolver()))
//                                .circuitBreaker(config -> config
//                                        .setName("rvBreaker")
//                                        .setFallbackUri("forward:/fallback/writers"))
//                                .retry(retryConfig -> retryConfig
//                                        .setRetries(3)
//                                        .setMethods(HttpMethod.GET)))
                        .uri("lb://WRITERSERVICE"))
                .route("tweetService", r -> r
                        .path("/api/v1.0/tweets/**")
//                        .filters(f -> f
//                                .requestRateLimiter(config -> config
//                                        .setRateLimiter(rateLimiter())
//                                        .setKeyResolver(keyResolver()))
//                                .circuitBreaker(config -> config
//                                        .setName("rvBreaker")
//                                        .setFallbackUri("forward:/fallback/tweets"))
//                                .retry(retryConfig -> retryConfig
//                                        .setRetries(3)
//                                        .setMethods(HttpMethod.GET)))
                        .uri("lb://tweetService"))
                .route("messageService", r -> r
                        .path("/api/v1.0/messages/**")
                        .and()
                        .method(HttpMethod.GET)
//                        .filters(f -> f
//                                .requestRateLimiter(config -> config
//                                        .setRateLimiter(rateLimiter())
//                                        .setKeyResolver(keyResolver()))
//                                .circuitBreaker(config -> config
//                                        .setName("rvBreaker")
//                                        .setFallbackUri("forward:/fallback/messages"))
//                                .retry(retryConfig -> retryConfig
//                                        .setRetries(3)
//                                        .setMethods(HttpMethod.GET)))
                        .uri("lb://messageService"))
                .route("tweetService", r -> r
                        .path("/api/v1.0/markers/**")
//                        .filters(f -> f
//                                .requestRateLimiter(config -> config
//                                        .setRateLimiter(rateLimiter())
//                                        .setKeyResolver(keyResolver()))
//                                .circuitBreaker(config -> config
//                                        .setName("rvBreaker")
//                                        .setFallbackUri("forward:/fallback/markers"))
//                                .retry(retryConfig -> retryConfig
//                                        .setRetries(3)
//                                        .setMethods(HttpMethod.GET)))
                        .uri("lb://markerService"))
                .route("tweetMarkersService", r -> r
                        .path("/api/v1.0/tweet-markers/**")
//                        .filters(f -> f
//                                .requestRateLimiter(config -> config
//                                        .setRateLimiter(rateLimiter())
//                                        .setKeyResolver(keyResolver()))
//                                .circuitBreaker(config -> config
//                                        .setName("rvBreaker")
//                                        .setFallbackUri("forward:/fallback/markers"))
//                                .retry(retryConfig -> retryConfig
//                                        .setRetries(3)
//                                        .setMethods(HttpMethod.GET)))
                        .uri("lb://tweetMarkersService"))
                .build();
    }
}
