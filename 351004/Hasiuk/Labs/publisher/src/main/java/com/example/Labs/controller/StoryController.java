package com.example.Labs.controller;
import com.example.Labs.dto.request.StoryRequestTo;
import com.example.Labs.dto.response.StoryResponseTo;
import com.example.Labs.entity.Editor;
import com.example.Labs.repository.EditorRepository;
import com.example.Labs.service.StoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/api/v1.0/stories", "/api/v2.0/stories"})
@RequiredArgsConstructor
public class StoryController {
    private final StoryService service;
    private final EditorRepository editorRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoryResponseTo create(@Valid @RequestBody StoryRequestTo request) {
        if (isV2()) checkOwnership(request.getEditorId());
        return service.create(request);
    }

    @GetMapping
    public List<StoryResponseTo> getAll(Pageable pageable) {
        return service.getAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    public StoryResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public StoryResponseTo update(@PathVariable Long id, @Valid @RequestBody StoryRequestTo request) {
        if (isV2()) {
            StoryResponseTo existing = service.getById(id);
            checkOwnership(existing.getEditorId());
        }
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (isV2()) {
            StoryResponseTo existing = service.getById(id);
            checkOwnership(existing.getEditorId());
        }
        service.delete(id);
    }

    private boolean isV2() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && !auth.getPrincipal().equals("anonymousUser");
    }

    private void checkOwnership(Long editorId) {
        String currentLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            Editor owner = editorRepository.findById(editorId).orElseThrow();
            if (!owner.getLogin().equals(currentLogin)) throw new RuntimeException("403 Forbidden");
        }
    }
}