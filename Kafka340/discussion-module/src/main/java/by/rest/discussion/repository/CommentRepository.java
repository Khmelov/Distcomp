package by.rest.discussion.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CommentRepository {
    
    private final Map<String, Object> storage = new HashMap<>();
    
    public void save(Object comment) {
        storage.put("comment", comment);
    }
    
    public Object find() {
        return storage.get("comment");
    }
}