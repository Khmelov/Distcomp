package com.apigateway.cache;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class PublisherCacheWebFilterTests {

    @Test
    void returnsCachedBodyWithoutCallingDownstream() {
        FakePublisherCacheService cacheService = new FakePublisherCacheService();
        cacheService.store.put("test:GET:/api/v1.0/writers", "[]");
        PublisherCacheWebFilter filter = new PublisherCacheWebFilter(cacheService);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1.0/writers").build()
        );
        WebFilterChain chain = currentExchange -> Mono.error(new AssertionError("Downstream should not be called"));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getHeaders().getFirst("X-Cache")).isEqualTo("HIT");
        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .expectNext("[]")
                .verifyComplete();
    }

    @Test
    void cachesSuccessfulJsonGetResponse() {
        FakePublisherCacheService cacheService = new FakePublisherCacheService();
        PublisherCacheWebFilter filter = new PublisherCacheWebFilter(cacheService);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1.0/tweets?size=10").build()
        );
        WebFilterChain chain = currentExchange -> {
            byte[] body = "[{\"id\":1}]".getBytes(StandardCharsets.UTF_8);
            currentExchange.getResponse().setStatusCode(HttpStatus.OK);
            currentExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return currentExchange.getResponse().writeWith(Mono.just(
                    currentExchange.getResponse().bufferFactory().wrap(body)
            ));
        };

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getHeaders().getFirst("X-Cache")).isEqualTo("MISS");
        assertThat(cacheService.store)
                .containsEntry("test:GET:/api/v1.0/tweets?size=10", "[{\"id\":1}]");
    }

    @Test
    void evictsCacheAfterSuccessfulWrite() {
        FakePublisherCacheService cacheService = new FakePublisherCacheService();
        PublisherCacheWebFilter filter = new PublisherCacheWebFilter(cacheService);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/v1.0/messages").build()
        );
        WebFilterChain chain = currentExchange -> {
            currentExchange.getResponse().setStatusCode(HttpStatus.CREATED);
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(cacheService.evicted).isTrue();
    }

    private static class FakePublisherCacheService extends PublisherCacheService {

        private final Map<String, String> store = new ConcurrentHashMap<>();
        private boolean evicted;

        private FakePublisherCacheService() {
            super(null, true, 60, "test:");
        }

        @Override
        public Mono<String> get(String key) {
            return Mono.justOrEmpty(store.get(key));
        }

        @Override
        public Mono<Boolean> put(String key, String value) {
            store.put(key, value);
            return Mono.just(true);
        }

        @Override
        public Mono<Long> evictAll() {
            evicted = true;
            store.clear();
            return Mono.just(1L);
        }
    }
}
