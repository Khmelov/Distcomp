package by.bsuir.task361.publisher.controller;

import by.bsuir.task361.publisher.dto.request.TagRequestTo;
import by.bsuir.task361.publisher.dto.response.TagResponseTo;
import by.bsuir.task361.publisher.service.SecuredTagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/tags")
public class TagV2Controller {
    private final SecuredTagService securedTagService;

    public TagV2Controller(SecuredTagService securedTagService) {
        this.securedTagService = securedTagService;
    }

    @PostMapping
    public ResponseEntity<TagResponseTo> create(@Valid @RequestBody TagRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(securedTagService.create(request));
    }

    @GetMapping
    public List<TagResponseTo> findAll() {
        return securedTagService.findAll();
    }

    @GetMapping("/{id}")
    public TagResponseTo findById(@PathVariable Long id) {
        return securedTagService.findById(id);
    }

    @PutMapping
    public TagResponseTo update(@Valid @RequestBody TagRequestTo request) {
        return securedTagService.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        securedTagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
