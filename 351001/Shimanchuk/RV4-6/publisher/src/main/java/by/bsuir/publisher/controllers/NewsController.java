package by.bsuir.publisher.controllers;

import by.bsuir.publisher.dto.requests.NewsRequestDto;
import by.bsuir.publisher.dto.responses.NewsResponseDto;
import by.bsuir.publisher.exceptions.EntityExistsException;
import by.bsuir.publisher.exceptions.Comments;
import by.bsuir.publisher.exceptions.NoEntityExistsException;
import by.bsuir.publisher.services.NewsService;
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
@RequestMapping("/api/v1.0/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @PostMapping
    public ResponseEntity<NewsResponseDto> create(@RequestBody NewsRequestDto news) throws EntityExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.create(news));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponseDto> read(@PathVariable("id") Long id) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(newsService.read(id).orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException)));
    }

    @GetMapping
    public ResponseEntity<List<NewsResponseDto>> read() {
        return ResponseEntity.status(HttpStatus.OK).body(newsService.readAll());
    }

    @PutMapping
    public ResponseEntity<NewsResponseDto> update(@RequestBody NewsRequestDto news) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(newsService.update(news));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable("id") Long id) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(newsService.delete(id));
    }
}
