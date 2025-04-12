package by.yelkin.TopicService.controller;

import by.yelkin.TopicService.dto.creator.CreatorRq;
import by.yelkin.TopicService.dto.creator.CreatorRs;
import by.yelkin.TopicService.dto.creator.CreatorUpdateRq;
import by.yelkin.TopicService.service.CreatorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1.0/creators")
public class CreatorController {
    private final CreatorService creatorService;

    @PostMapping
    public ResponseEntity<CreatorRs> create(@Valid @RequestBody CreatorRq rq) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creatorService.create(rq));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreatorRs> readById(@Valid @NotNull @PathVariable("id") Long id) {
        return ResponseEntity.ok().body(creatorService.readById(id));
    }

    @GetMapping
    public ResponseEntity<List<CreatorRs>> readAll() {
        return ResponseEntity.ok().body(creatorService.readAll());
    }

    @PutMapping
    public ResponseEntity<CreatorRs> update(@Valid @RequestBody CreatorUpdateRq rq) {
        return ResponseEntity.ok().body(creatorService.update(rq));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@Valid @NotNull @PathVariable("id") Long id) {
        creatorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
