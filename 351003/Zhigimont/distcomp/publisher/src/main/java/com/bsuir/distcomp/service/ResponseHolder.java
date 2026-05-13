package com.bsuir.distcomp.service;

import com.bsuir.distcomp.dto.CommentListResponseTo;
import com.bsuir.distcomp.dto.CommentResponseTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ResponseHolder {

    private final Map<String, CompletableFuture<CommentResponseTo>> singleMap = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<CommentListResponseTo>> listMap = new ConcurrentHashMap<>();

    public CompletableFuture<CommentResponseTo> createSingle(String id) {
        CompletableFuture<CommentResponseTo> future = new CompletableFuture<>();

        // Add timeout to clean up automatically
        future.orTimeout(15, TimeUnit.SECONDS)
                .exceptionally(ex -> null);

        singleMap.put(id, future);
        log.info("🔷 Created single future: id={}, total pending: {}", id, singleMap.size());
        return future;
    }

    public CompletableFuture<CommentListResponseTo> createList(String id) {
        CompletableFuture<CommentListResponseTo> future = new CompletableFuture<>();

        // Add timeout to clean up automatically
        future.orTimeout(15, TimeUnit.SECONDS)
                .exceptionally(ex -> null);

        listMap.put(id, future);
        log.info("🔷 Created list future: id={}, total pending: {}", id, listMap.size());
        return future;
    }

    public void completeSingle(String id, CommentResponseTo response) {
        log.info("🔵 Attempting to complete single future: id={}", id);
        CompletableFuture<CommentResponseTo> future = singleMap.remove(id);
        if (future != null) {
            boolean completed = future.complete(response);
            log.info("✅ Completed single future: id={}, success={}, remaining: {}",
                    id, completed, singleMap.size());
        } else {
            log.warn("⚠️ No single future found for id: {} (already completed or timed out)", id);
            log.warn("Available single correlationIds: {}", singleMap.keySet());
            log.warn("Available list correlationIds: {}", listMap.keySet());
        }
    }

    public void completeList(String id, CommentListResponseTo response) {
        log.info("🔵 Attempting to complete list future: id={}", id);
        CompletableFuture<CommentListResponseTo> future = listMap.remove(id);
        if (future != null) {
            boolean completed = future.complete(response);
            log.info("✅ Completed list future: id={}, success={}, remaining: {}",
                    id, completed, listMap.size());
        } else {
            log.warn("⚠️ No list future found for id: {} (already completed or timed out)", id);
            log.warn("Available list correlationIds: {}", listMap.keySet());
            log.warn("Available single correlationIds: {}", singleMap.keySet());
        }
    }

    // Add a method to check pending futures
    public int getPendingSingleCount() {
        return singleMap.size();
    }

    public int getPendingListCount() {
        return listMap.size();
    }
}