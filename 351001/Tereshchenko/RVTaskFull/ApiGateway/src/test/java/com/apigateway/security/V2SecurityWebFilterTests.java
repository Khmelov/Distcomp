package com.apigateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class V2SecurityWebFilterTests {

    @Test
    void rejectsProtectedV2EndpointWithoutBearerToken() {
        V2SecurityWebFilter filter = new V2SecurityWebFilter(null, null, null, new ObjectMapper());
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v2.0/tweets").build()
        );
        WebFilterChain chain = currentExchange -> Mono.error(new AssertionError("Downstream should not be called"));

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getStatusCode().value()).isEqualTo(401);
        StepVerifier.create(exchange.getResponse().getBodyAsString())
                .assertNext(body -> {
                    assertThat(body).contains("errorMessage");
                    assertThat(body).contains("40101");
                })
                .verifyComplete();
    }

    @Test
    void letsV1EndpointPassWithoutBearerToken() {
        V2SecurityWebFilter filter = new V2SecurityWebFilter(null, null, null, new ObjectMapper());
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1.0/tweets").build()
        );
        WebFilterChain chain = currentExchange -> Mono.empty();

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
    }
}
