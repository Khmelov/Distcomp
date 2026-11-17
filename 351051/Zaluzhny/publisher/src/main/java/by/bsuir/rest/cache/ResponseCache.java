package by.bsuir.rest.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;

public class ResponseCache {

    public static final String ALL = "all";
    public static final String USER = "user";
    public static final String STORY = "story";
    public static final String MARKER = "marker";
    public static final String MESSAGE = "message";

    private final StringCache store;
    private final ObjectMapper objectMapper;

    public ResponseCache(StringCache store, ObjectMapper objectMapper) {
        this.store = store;
        this.objectMapper = objectMapper;
    }

    public static ResponseCache memory(ObjectMapper objectMapper) {
        return new ResponseCache(new MemoryStringCache(), objectMapper);
    }

    public <T> Optional<T> get(String region, Object key, Class<T> type) {
        String json = store.get(redisKey(region, key));
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, type));
        } catch (JsonProcessingException exception) {
            store.delete(redisKey(region, key));
            return Optional.empty();
        }
    }

    public <T> Optional<List<T>> getList(String region, Object key, Class<T> elementType) {
        String json = store.get(redisKey(region, key));
        if (json == null) {
            return Optional.empty();
        }
        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
        try {
            return Optional.of(objectMapper.readValue(json, listType));
        } catch (JsonProcessingException exception) {
            store.delete(redisKey(region, key));
            return Optional.empty();
        }
    }

    public void put(String region, Object key, Object value) {
        afterCommit(() -> store.put(redisKey(region, key), write(value)));
    }

    public void evict(String region, Object key) {
        afterCommit(() -> store.delete(redisKey(region, key)));
    }

    private String write(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Cannot write cache value", exception);
        }
    }

    private static String redisKey(String region, Object key) {
        return "distcomp:" + region + ":" + key;
    }

    private static void afterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}
