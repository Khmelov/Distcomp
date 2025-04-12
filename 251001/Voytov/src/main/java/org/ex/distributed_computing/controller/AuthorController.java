package org.ex.distributed_computing.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.dto.request.AuthorRequestDTO;
import org.ex.distributed_computing.dto.request.IdDTO;
import org.ex.distributed_computing.dto.response.AuthorResponseDTO;
import org.ex.distributed_computing.mapper.AuthorMapper;
import org.ex.distributed_computing.model.Author;
import org.ex.distributed_computing.repository.AuthorRepository;
import org.ex.distributed_computing.service.AuthorService;
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
@RequestMapping("/authors")
public class AuthorController {

  private final AuthorService authorService;

  @GetMapping
  public List<AuthorResponseDTO> getAllAuthors() {
    return authorService.getAllAuthors();
  }

  @GetMapping("/{id}")
  public ResponseEntity<AuthorResponseDTO> getAuthorById(@PathVariable Long id) {
    return ResponseEntity.ok(authorService.getAuthorById(id));
  }

  @PostMapping
  public ResponseEntity<AuthorResponseDTO> createAuthor(@Valid @RequestBody AuthorRequestDTO requestDTO) {
    var author = authorService.createAuthor(requestDTO);
    return ResponseEntity.status(HttpStatus.CREATED.value()).body(author);
  }

  @PutMapping()
  public ResponseEntity<AuthorResponseDTO> updateAuthor(@Valid @RequestBody AuthorRequestDTO dto) {
    return ResponseEntity.ok(authorService.updateAuthor(dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
    authorService.deleteAuthor(id);
    return ResponseEntity.noContent().build();
  }
}
