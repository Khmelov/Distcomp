package by.rest.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/v1.0/editors")
public class EditorController {
    
    private final Map<Long, Editor> database = new HashMap<>();
    private Long nextId = 1L;
    
    @PostMapping
    public ResponseEntity<EditorResponse> create(@RequestBody EditorRequest req) {
        // Валидация
        if (req.getLogin() == null || req.getLogin().length() < 2) {
            return ResponseEntity.badRequest().build();
        }
        
        // Проверка уникальности логина
        boolean loginExists = database.values().stream()
                .anyMatch(editor -> editor.getLogin().equals(req.getLogin()));
        
        if (loginExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }
        
        // Создание
        Editor editor = new Editor();
        editor.setId(nextId++);
        editor.setLogin(req.getLogin());
        editor.setPassword(req.getPassword());
        editor.setFirstname(req.getFirstname());
        editor.setLastname(req.getLastname());
        
        database.put(editor.getId(), editor);
        
        // Response
        EditorResponse response = new EditorResponse();
        response.setId(editor.getId());
        response.setLogin(editor.getLogin());
        response.setPassword(editor.getPassword());
        response.setFirstname(editor.getFirstname());
        response.setLastname(editor.getLastname());
        
        return ResponseEntity.created(URI.create("/api/v1.0/editors/" + response.getId()))
                .body(response);
    }
    
    @GetMapping
    public List<EditorResponse> getAll() {
        return database.values().stream()
                .map(this::toResponse)
                .toList();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EditorResponse> getById(@PathVariable("id") Long id) {
        Editor editor = database.get(id);
        if (editor == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(editor));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EditorResponse> update(
            @PathVariable("id") Long id,
            @RequestBody EditorRequest req) {
        
        // 1. Проверяем существование редактора
        Editor existingEditor = database.get(id);
        if (existingEditor == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 2. Валидация
        if (req.getLogin() == null || req.getLogin().length() < 2) {
            return ResponseEntity.badRequest().build();
        }
        
        // 3. Проверка уникальности логина (если логин изменился)
        if (!existingEditor.getLogin().equals(req.getLogin())) {
            boolean loginExists = database.values().stream()
                    .anyMatch(editor -> 
                        !editor.getId().equals(id) && 
                        editor.getLogin().equals(req.getLogin()));
            
            if (loginExists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(null);
            }
        }
        
        // 4. Обновляем данные
        existingEditor.setLogin(req.getLogin());
        existingEditor.setPassword(req.getPassword());
        existingEditor.setFirstname(req.getFirstname());
        existingEditor.setLastname(req.getLastname());
        
        // 5. Response
        return ResponseEntity.ok(toResponse(existingEditor));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        if (!database.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        
        database.remove(id);
        return ResponseEntity.noContent().build();
    }
    
    private EditorResponse toResponse(Editor editor) {
        EditorResponse response = new EditorResponse();
        response.setId(editor.getId());
        response.setLogin(editor.getLogin());
        response.setPassword(editor.getPassword());
        response.setFirstname(editor.getFirstname());
        response.setLastname(editor.getLastname());
        return response;
    }
    
    // Внутренние классы остаются без изменений
    static class Editor {
        private Long id;
        private String login;
        private String password;
        private String firstname;
        private String lastname;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstname() { return firstname; }
        public void setFirstname(String firstname) { this.firstname = firstname; }
        public String getLastname() { return lastname; }
        public void setLastname(String lastname) { this.lastname = lastname; }
    }
    
    static class EditorRequest {
        private String login;
        private String password;
        private String firstname;
        private String lastname;
        
        // Getters and Setters
        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstname() { return firstname; }
        public void setFirstname(String firstname) { this.firstname = firstname; }
        public String getLastname() { return lastname; }
        public void setLastname(String lastname) { this.lastname = lastname; }
    }
    
    static class EditorResponse {
        private Long id;
        private String login;
        private String password;
        private String firstname;
        private String lastname;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstname() { return firstname; }
        public void setFirstname(String firstname) { this.firstname = firstname; }
        public String getLastname() { return lastname; }
        public void setLastname(String lastname) { this.lastname = lastname; }
    }
}