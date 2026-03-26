package com.example.task310.controller;

import com.example.task310.model.Mark;
import com.example.task310.repository.MarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/test")
@RequiredArgsConstructor
public class TestController {

    private final MarkRepository markRepository;

    // Эндпоинт для создания метки (вызывается тестами)
    @PostMapping("/marks/{name}")
    @Transactional
    public ResponseEntity<String> createMark(@PathVariable String name) {
        if (markRepository.findByName(name).isEmpty()) {
            Mark mark = new Mark();
            mark.setName(name);
            markRepository.save(mark);
            return ResponseEntity.ok("Метка создана: " + name);
        }
        return ResponseEntity.ok("Метка уже существует: " + name);
    }

    // Эндпоинт для удаления метки (вызывается тестами)
    @DeleteMapping("/marks/{name}")
    @Transactional
    public ResponseEntity<String> deleteMark(@PathVariable String name) {
        markRepository.findByName(name).ifPresent(mark -> {
            mark.getNews().clear();
            markRepository.delete(mark);
        });
        return ResponseEntity.ok("Метка удалена: " + name);
    }

    // Эндпоинт для удаления всех тестовых меток
    @DeleteMapping("/marks")
    @Transactional
    public ResponseEntity<String> deleteAllTestMarks() {
        List<Mark> testMarks = markRepository.findAllTestMarks();
        for (Mark mark : testMarks) {
            mark.getNews().clear();
            markRepository.delete(mark);
        }
        return ResponseEntity.ok("Удалено тестовых меток: " + testMarks.size());
    }

    // Эндпоинт для проверки существования метки
    @GetMapping("/marks/{name}")
    public ResponseEntity<Boolean> checkMark(@PathVariable String name) {
        return ResponseEntity.ok(markRepository.findByName(name).isPresent());
    }
}