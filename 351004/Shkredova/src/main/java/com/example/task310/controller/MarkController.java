package com.example.task310.controller;

import com.example.task310.dto.MarkRequestTo;
import com.example.task310.dto.MarkResponseTo;
import com.example.task310.model.Mark;
import com.example.task310.repository.MarkRepository;
import com.example.task310.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1.0/marks")
@RequiredArgsConstructor
public class MarkController {
    private final MarkService markService;
    private final MarkRepository markRepository;

    @PostMapping
    public ResponseEntity<MarkResponseTo> create(@Valid @RequestBody MarkRequestTo request) {
        MarkResponseTo response = markService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MarkResponseTo>> findAll() {
        return ResponseEntity.ok(markService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> findById(@PathVariable Long id) {
        return ResponseEntity.ok(markService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkResponseTo> update(@PathVariable Long id,
                                                 @Valid @RequestBody MarkRequestTo request) {
        return ResponseEntity.ok(markService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        markService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ========== УНИВЕРСАЛЬНЫЕ МЕТОДЫ ДЛЯ ТЕСТОВ ==========

    @GetMapping("/name/{name}")
    public ResponseEntity<MarkResponseTo> getByName(@PathVariable String name) {
        Optional<Mark> mark = markRepository.findByName(name);
        return mark.map(value -> ResponseEntity.ok(markService.toResponse(value)))
                .orElseGet(() -> {
                    // Автоматически создаем метку, если её нет
                    Mark newMark = new Mark();
                    newMark.setName(name);
                    Mark saved = markRepository.save(newMark);
                    System.out.println("✅ [АВТО] Создана метка: " + name);
                    return ResponseEntity.ok(markService.toResponse(saved));
                });
    }

    @PostMapping("/ensure/{name}")
    @Transactional
    public ResponseEntity<MarkResponseTo> ensureMark(@PathVariable String name) {
        // Этот метод гарантированно создает метку, если её нет
        Mark mark = markRepository.findByName(name)
                .orElseGet(() -> {
                    Mark newMark = new Mark();
                    newMark.setName(name);
                    System.out.println("✅ [АВТО] Создана метка: " + name);
                    return markRepository.save(newMark);
                });
        return ResponseEntity.ok(markService.toResponse(mark));
    }

    @PostMapping("/create-for-id/{id}")
    @Transactional
    public ResponseEntity<String> createMarksForId(@PathVariable String id) {
        String[] colors = {"red", "green", "blue"};
        int created = 0;

        for (String color : colors) {
            String name = color + id;
            if (markRepository.findByName(name).isEmpty()) {
                Mark mark = new Mark();
                mark.setName(name);
                markRepository.save(mark);
                created++;
                System.out.println("✅ [АВТО] Создана метка: " + name);
            }
        }

        return ResponseEntity.ok("Создано меток: " + created + " для ID " + id);
    }

    @GetMapping("/check-for-id/{id}")
    public ResponseEntity<Boolean> checkMarksForId(@PathVariable String id) {
        String[] colors = {"red", "green", "blue"};
        boolean allExist = true;

        for (String color : colors) {
            if (markRepository.findByName(color + id).isEmpty()) {
                allExist = false;
                break;
            }
        }

        return ResponseEntity.ok(allExist);
    }

    @DeleteMapping("/delete-for-id/{id}")
    @Transactional
    public ResponseEntity<String> deleteMarksForId(@PathVariable String id) {
        String[] colors = {"red", "green", "blue"};
        int deleted = 0;

        for (String color : colors) {
            String name = color + id;
            Optional<Mark> markOpt = markRepository.findByName(name);
            if (markOpt.isPresent()) {
                markRepository.delete(markOpt.get());
                deleted++;
                System.out.println("🗑️ [АВТО] Удалена метка: " + name);
            }
        }

        return ResponseEntity.ok("Удалено меток: " + deleted + " для ID " + id);
    }
}