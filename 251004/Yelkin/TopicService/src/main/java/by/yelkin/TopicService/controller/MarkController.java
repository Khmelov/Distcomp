package by.yelkin.TopicService.controller;

import by.yelkin.TopicService.dto.mark.MarkRq;
import by.yelkin.TopicService.dto.mark.MarkRs;
import by.yelkin.TopicService.dto.mark.MarkUpdateRq;
import by.yelkin.TopicService.service.MarkService;
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
@RequestMapping("/api/v1.0/marks")
public class MarkController {
    private final MarkService markService;

    @PostMapping
    public ResponseEntity<MarkRs> create(@Valid @RequestBody MarkRq rq) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(markService.create(rq));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkRs> readById(@Valid @NotNull @PathVariable("id") Long id) {
        return ResponseEntity.ok().body(markService.readById(id));
    }

    @GetMapping
    public ResponseEntity<List<MarkRs>> readAll() {
        return ResponseEntity.ok().body(markService.readAll());
    }

    @PutMapping
    public ResponseEntity<MarkRs> update(@Valid @RequestBody MarkUpdateRq rq) {
        return ResponseEntity.ok().body(markService.update(rq));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@Valid @NotNull @PathVariable("id") Long id) {
        markService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}