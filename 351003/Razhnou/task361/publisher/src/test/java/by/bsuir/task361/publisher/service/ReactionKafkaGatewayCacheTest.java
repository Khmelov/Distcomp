package by.bsuir.task361.publisher.service;

import by.bsuir.task361.publisher.config.CacheNames;
import by.bsuir.task361.publisher.dto.ReactionState;
import by.bsuir.task361.publisher.dto.request.ReactionRequestTo;
import by.bsuir.task361.publisher.dto.response.ReactionResponseTo;
import by.bsuir.task361.publisher.kafka.dto.ReactionKafkaRequest;
import by.bsuir.task361.publisher.kafka.dto.ReactionKafkaResponse;
import by.bsuir.task361.publisher.kafka.dto.ReactionPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReactionKafkaGatewayCacheTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private PublisherCacheService cacheService;
    private ReactionKafkaGateway gateway;

    @BeforeEach
    void setUp() {
        cacheService = new PublisherCacheService(new ConcurrentMapCacheManager(CacheNames.REACTIONS));
        gateway = new ReactionKafkaGateway(kafkaTemplate, objectMapper, cacheService, "InTopic", 1000);
    }

    @Test
    void findByIdCachesResponseAndSkipsKafkaOnSecondRead() {
        doAnswer(invocation -> {
            ReactionKafkaRequest request = objectMapper.readValue(invocation.getArgument(2, String.class), ReactionKafkaRequest.class);
            gateway.complete(new ReactionKafkaResponse(
                    request.requestId(),
                    true,
                    200,
                    null,
                    null,
                    new ReactionPayload(5L, 11L, "cached reaction", ReactionState.APPROVE),
                    null
            ));
            return CompletableFuture.completedFuture(null);
        }).when(kafkaTemplate).send(anyString(), anyString(), anyString());

        ReactionResponseTo first = gateway.findById(5L);
        ReactionResponseTo second = gateway.findById(5L);

        assertEquals(first, second);
        assertEquals(first, cacheService.get(CacheNames.REACTIONS, 5L, ReactionResponseTo.class));
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), anyString());
    }

    @Test
    void updateRefreshesCachedReaction() {
        doAnswer(invocation -> {
            ReactionKafkaRequest request = objectMapper.readValue(invocation.getArgument(2, String.class), ReactionKafkaRequest.class);
            gateway.complete(new ReactionKafkaResponse(
                    request.requestId(),
                    true,
                    200,
                    null,
                    null,
                    request.payload(),
                    null
            ));
            return CompletableFuture.completedFuture(null);
        }).when(kafkaTemplate).send(anyString(), anyString(), anyString());

        ReactionResponseTo response = gateway.update(new ReactionRequestTo(5L, 11L, "updated reaction", ReactionState.APPROVE));

        assertEquals(response, cacheService.get(CacheNames.REACTIONS, 5L, ReactionResponseTo.class));
        verify(kafkaTemplate).send(anyString(), anyString(), anyString());
    }

    @Test
    void deleteEvictsCachedReaction() {
        cacheService.put(CacheNames.REACTIONS, 5L, new ReactionResponseTo(5L, 11L, "cached reaction", ReactionState.APPROVE));
        doAnswer(invocation -> {
            ReactionKafkaRequest request = objectMapper.readValue(invocation.getArgument(2, String.class), ReactionKafkaRequest.class);
            gateway.complete(new ReactionKafkaResponse(request.requestId(), true, 204, null, null, null, null));
            return CompletableFuture.completedFuture(null);
        }).when(kafkaTemplate).send(anyString(), anyString(), anyString());

        gateway.delete(5L);

        assertNull(cacheService.get(CacheNames.REACTIONS, 5L, ReactionResponseTo.class));
        verify(kafkaTemplate).send(anyString(), anyString(), anyString());
    }
}
