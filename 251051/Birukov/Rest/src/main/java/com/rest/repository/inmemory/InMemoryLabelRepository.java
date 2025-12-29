package com.rest.repository.inmemory;

import com.rest.entity.Label;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryLabelRepository{
    
    private final Map<Long, Label> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public Label save(Label label) {
		if (label == null) {
			throw new IllegalArgumentException("Label cannot be null");
		}
		
        if (label.getId() == null) {
            label.setId(idGenerator.getAndIncrement());
        }
        storage.put(label.getId(), label);
        return label;
    }
    
    public Optional<Label> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }
    
    public List<Label> findAll() {
        return new ArrayList<>(storage.values());
    }
    
    public Label update(Label label) {
        if (!storage.containsKey(label.getId())) {
            throw new RuntimeException("Label not found with id: " + label.getId());
        }
        storage.put(label.getId(), label);
        return label;
    }
    
    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }
    
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
	
	public Optional<Label> findByName(String name) {
        return storage.values().stream()
            .filter(label -> label.getName().equals(name))
            .findFirst();
    }
    
    public boolean existsByName(String name) {
        return storage.values().stream()
            .anyMatch(label -> label.getName().equals(name));
    }
}