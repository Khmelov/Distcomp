package com.example.kafkademo.controller.v2;

import com.example.kafkademo.dto.response.CreatorResponseDto;
import com.example.kafkademo.entity.Creator;
import com.example.kafkademo.service.CreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2.0/creators")
public class CreatorControllerV2 {

    @Autowired
    private CreatorService creatorService;

    // Убираем @PreAuthorize - пусть обрабатывается через SecurityConfig
    @GetMapping
    public ResponseEntity<?> getAll() {
        // Проверяем аутентификацию вручную
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new com.example.kafkademo.dto.response.ErrorResponseDto(
                            "Authentication required", "40100"));
        }

        // Проверяем роль ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new com.example.kafkademo.dto.response.ErrorResponseDto(
                            "Access denied", "40301"));
        }

        List<CreatorResponseDto> creators = creatorService.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(creators);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #id == authentication.principal.id)")
    public ResponseEntity<CreatorResponseDto> getById(@PathVariable Long id) {
        return creatorService.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CreatorResponseDto> getMe() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Creator creator = creatorService.findByLogin(login).get();
        return ResponseEntity.ok(toDto(creator));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #id == authentication.principal.id)")
    public ResponseEntity<CreatorResponseDto> update(@PathVariable Long id, @RequestBody Creator creator) {
        if (!creatorService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        creator.setId(id);
        return ResponseEntity.ok(toDto(creatorService.save(creator)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!creatorService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        creatorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private CreatorResponseDto toDto(Creator creator) {
        CreatorResponseDto dto = new CreatorResponseDto();
        dto.setId(creator.getId());
        dto.setLogin(creator.getLogin());
        dto.setFirstName(creator.getFirstName());
        dto.setLastName(creator.getLastName());
        dto.setRole(creator.getRole().name());
        dto.setCreatedAt(creator.getCreatedAt());
        return dto;
    }
}