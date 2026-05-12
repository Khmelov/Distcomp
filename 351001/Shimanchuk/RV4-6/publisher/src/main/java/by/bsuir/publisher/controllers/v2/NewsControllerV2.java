package by.bsuir.publisher.controllers.v2;

import by.bsuir.publisher.dto.requests.NewsRequestDto;
import by.bsuir.publisher.dto.responses.NewsResponseDto;
import by.bsuir.publisher.exceptions.Comments;
import by.bsuir.publisher.exceptions.EntityExistsException;
import by.bsuir.publisher.exceptions.NoEntityExistsException;
import by.bsuir.publisher.services.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v2.0/news")
@RequiredArgsConstructor
public class NewsControllerV2 {

    private final NewsService newsService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @newsSecurity.isOwner(#dto, authentication.name)")
    public ResponseEntity<NewsResponseDto> create(@RequestBody NewsRequestDto dto) throws EntityExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.create(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NewsResponseDto> read(@PathVariable("id") Long id) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(newsService.read(id).orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException)));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NewsResponseDto>> read() {
        return ResponseEntity.status(HttpStatus.OK).body(newsService.readAll());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or @newsSecurity.isOwner(#dto, authentication.name)")
    public ResponseEntity<NewsResponseDto> update(@RequestBody NewsRequestDto dto) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(newsService.update(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @newsSecurity.isOwnerById(#id, authentication.name)")
    public ResponseEntity<Long> delete(@PathVariable("id") Long id) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(newsService.delete(id));
    }
}
