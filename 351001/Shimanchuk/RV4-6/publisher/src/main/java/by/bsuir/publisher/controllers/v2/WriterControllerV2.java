package by.bsuir.publisher.controllers.v2;

import by.bsuir.publisher.dto.requests.WriterRequestDto;
import by.bsuir.publisher.dto.responses.WriterResponseDto;
import by.bsuir.publisher.exceptions.Comments;
import by.bsuir.publisher.exceptions.NoEntityExistsException;
import by.bsuir.publisher.services.WriterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/writers")
@RequiredArgsConstructor
public class WriterControllerV2 {

    private final WriterService writerService;

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WriterResponseDto> read(@PathVariable("id") Long id) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(writerService.read(id).orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException)));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WriterResponseDto>> read() {
        return ResponseEntity.status(HttpStatus.OK).body(writerService.readAll());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or #dto.login == authentication.name")
    public ResponseEntity<WriterResponseDto> update(@RequestBody WriterRequestDto dto) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(writerService.update(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @writerSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<Long> delete(@PathVariable("id") Long id) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(writerService.delete(id));
    }
}
