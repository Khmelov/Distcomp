package org.ex.distributed_computing.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.dto.request.LabelRequestDTO;
import org.ex.distributed_computing.dto.response.LabelResponseDTO;
import org.ex.distributed_computing.service.LabelService;
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
@RequestMapping("/labels")
public class LabelController {

  private final LabelService labelService;

  @GetMapping
  public List<LabelResponseDTO> getAllLabels() {
    return labelService.getAllLabels();
  }

  @GetMapping("/{id}")
  public ResponseEntity<LabelResponseDTO> getLabelById(@PathVariable Long id) {
    return ResponseEntity.ok(labelService.getLabelById(id));
  }

  @PostMapping
  public ResponseEntity<LabelResponseDTO> createLabel(@Valid @RequestBody LabelRequestDTO requestDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(labelService.createLabel(requestDTO));
  }

  @PutMapping
  public ResponseEntity<LabelResponseDTO> updateLabel(@Valid @RequestBody LabelRequestDTO requestDTO) {
    return ResponseEntity.ok(labelService.updateLabel(requestDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
    labelService.deleteLabel(id);
    return ResponseEntity.noContent().build();
  }
}
