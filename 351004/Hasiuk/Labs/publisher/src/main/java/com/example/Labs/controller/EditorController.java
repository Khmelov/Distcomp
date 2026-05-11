package com.example.Labs.controller;
import com.example.Labs.dto.request.EditorRequestTo;
import com.example.Labs.dto.response.EditorResponseTo;
import com.example.Labs.service.EditorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/api/v1.0/editors", "/api/v2.0/editors"})
@RequiredArgsConstructor
public class EditorController {
    private final EditorService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EditorResponseTo create(@Valid @RequestBody EditorRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<EditorResponseTo> getAll(Pageable pageable) {
        return service.getAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    public EditorResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public EditorResponseTo update(@PathVariable Long id, @Valid @RequestBody EditorRequestTo request) {
        if (isV2()) checkProfileOwnership(id);
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (isV2()) checkProfileOwnership(id);
        service.delete(id);
    }

    private boolean isV2() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && !auth.getPrincipal().equals("anonymousUser");
    }

    private void checkProfileOwnership(Long id) {
        String currentLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            EditorResponseTo profile = service.getById(id);
            if (!profile.getLogin().equals(currentLogin)) throw new RuntimeException("403 Forbidden");
        }
    }
}