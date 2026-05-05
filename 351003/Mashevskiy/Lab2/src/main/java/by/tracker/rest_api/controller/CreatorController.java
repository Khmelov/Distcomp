package by.tracker.rest_api.controller;

import by.tracker.rest_api.entity.Creator;
import by.tracker.rest_api.repository.CreatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/creators")
public class CreatorController {

    @Autowired
    private CreatorRepository creatorRepository;

    @GetMapping
    public List<Creator> getAll() {
        return creatorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Creator> getById(@PathVariable Long id) {
        return creatorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Creator creator) {
        if (creator.getLogin() == null || creator.getLogin().length() < 2 || creator.getLogin().length() > 64) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", "Login must be between 2 and 64 characters");
            error.put("errorCode", 40001);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(error);
        }

        if (creator.getPassword() == null || creator.getPassword().length() < 8 || creator.getPassword().length() > 128) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", "Password must be between 8 and 128 characters");
            error.put("errorCode", 40002);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(error);
        }

        if (creator.getFirstname() == null || creator.getFirstname().length() < 2 || creator.getFirstname().length() > 64) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", "Firstname must be between 2 and 64 characters");
            error.put("errorCode", 40003);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(error);
        }

        if (creator.getLastname() == null || creator.getLastname().length() < 2 || creator.getLastname().length() > 64) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", "Lastname must be between 2 and 64 characters");
            error.put("errorCode", 40004);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(error);
        }

        if (creatorRepository.existsByLogin(creator.getLogin())) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", "Creator with this login already exists");
            error.put("errorCode", 40301);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        Creator saved = creatorRepository.save(creator);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Creator creator) {
        if (creator.getId() == null || !creatorRepository.existsById(creator.getId())) {
            return ResponseEntity.notFound().build();
        }

        if (creator.getLogin() != null && (creator.getLogin().length() < 2 || creator.getLogin().length() > 64)) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", "Login must be between 2 and 64 characters");
            error.put("errorCode", 40001);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(error);
        }

        if (creator.getPassword() != null && (creator.getPassword().length() < 8 || creator.getPassword().length() > 128)) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", "Password must be between 8 and 128 characters");
            error.put("errorCode", 40002);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(error);
        }

        Creator existing = creatorRepository.findById(creator.getId()).get();

        if (creator.getLogin() != null && !creator.getLogin().equals(existing.getLogin())) {
            if (creatorRepository.existsByLogin(creator.getLogin())) {
                Map<String, Object> error = new HashMap<>();
                error.put("errorMessage", "Creator with this login already exists");
                error.put("errorCode", 40301);
                error.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            existing.setLogin(creator.getLogin());
        }
        if (creator.getPassword() != null) existing.setPassword(creator.getPassword());
        if (creator.getFirstname() != null) existing.setFirstname(creator.getFirstname());
        if (creator.getLastname() != null) existing.setLastname(creator.getLastname());

        return ResponseEntity.ok(creatorRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!creatorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        creatorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}