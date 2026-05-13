package by.bsuir.publisher.controllers;

import by.bsuir.publisher.dto.requests.WriterRequestDto;
import by.bsuir.publisher.dto.responses.WriterResponseDto;
import by.bsuir.publisher.exceptions.EntityExistsException;
import by.bsuir.publisher.exceptions.Comments;
import by.bsuir.publisher.exceptions.NoEntityExistsException;
import by.bsuir.publisher.services.WriterService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1.0/writers")
@RequiredArgsConstructor
public class WriterController {
    private final WriterService writerService;

    @PostMapping
    public ResponseEntity<WriterResponseDto> create(@RequestBody WriterRequestDto writer) throws EntityExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(writerService.create(writer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WriterResponseDto> read(@PathVariable("id") Long id) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(writerService.read(id).orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException)));
    }

    @GetMapping
    public ResponseEntity<List<WriterResponseDto>> read() {
        return ResponseEntity.status(HttpStatus.OK).body(writerService.readAll());
    }

    @PutMapping
    public ResponseEntity<WriterResponseDto> update(@RequestBody WriterRequestDto writer) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(writerService.update(writer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable("id") Long id) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(writerService.delete(id));
    }
}
