package org.ex.distributed_computing.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.dto.request.MarkRequestDTO;
import org.ex.distributed_computing.dto.response.MarkResponseDTO;
import org.ex.distributed_computing.service.MarkService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/marks")
public class MarkController {

  private final MarkService markService;

  @GetMapping
  public List<MarkResponseDTO> getAllMarks() {
    return markService.getAllMarks();
  }

  @GetMapping("/{id}")
  public ResponseEntity<MarkResponseDTO> getMarkById(@PathVariable Long id) {
    return ResponseEntity.ok(markService.getMarkById(id));
  }

  @PostMapping
  public ResponseEntity<MarkResponseDTO> createMark(@Valid @RequestBody MarkRequestDTO requestDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(markService.createMark(requestDTO));
  }

  @PutMapping
  public ResponseEntity<MarkResponseDTO> updateMark(@Valid @RequestBody MarkRequestDTO requestDTO) {
    return ResponseEntity.ok(markService.updateMark(requestDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMark(@PathVariable Long id) {
    markService.deleteMark(id);
    return ResponseEntity.noContent().build();
  }
}
