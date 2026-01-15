package by.bsuir.entitiesapp.service;

import by.bsuir.entitiesapp.dto.TweetRequestTo;
import by.bsuir.entitiesapp.dto.TweetResponseTo;
import by.bsuir.entitiesapp.entity.Tweet;
import by.bsuir.entitiesapp.exception.BadRequestException;
import by.bsuir.entitiesapp.exception.BusinessLogicException;
import by.bsuir.entitiesapp.exception.NotFoundException;
import by.bsuir.entitiesapp.repository.AuthorRepository;
import by.bsuir.entitiesapp.repository.TweetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TweetService {

    private final TweetRepository repository;
    private final AuthorRepository authorRepository;

    public TweetService(TweetRepository repository, AuthorRepository authorRepository) {
        this.repository = repository;
        this.authorRepository = authorRepository;
    }

    @Transactional
    public TweetResponseTo create(TweetRequestTo dto) {
        validate(dto);

        if (!authorRepository.existsById(dto.authorId)) {
            throw new BadRequestException("Invalid author", "40002");
        }

        if (repository.findAll().stream().anyMatch(t -> t.getTitle().equals(dto.title))) {
            throw new BusinessLogicException("Duplicate title", "40301");
        }

        Tweet tweet = new Tweet();
        tweet.setTitle(dto.title);
        tweet.setContent(dto.content);
        tweet.setAuthorId(dto.authorId);

        return toResponse(repository.save(tweet));
    }

    public TweetResponseTo get(Long id) {
        Tweet tweet = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found", "40401"));
        return toResponse(tweet);
    }

    public List<TweetResponseTo> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TweetResponseTo update(Long id, TweetRequestTo dto) {
        validate(dto);

        Tweet tweet = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found", "40401"));

        if (!authorRepository.existsById(dto.authorId)) {
            throw new BadRequestException("Invalid author", "40002");
        }

        if (repository.findAll().stream()
                .anyMatch(t -> !t.getId().equals(id) && t.getTitle().equals(dto.title))) {
            throw new BusinessLogicException("Duplicate title", "40301");
        }

        tweet.setTitle(dto.title);
        tweet.setContent(dto.content);
        tweet.setAuthorId(dto.authorId);

        return toResponse(repository.save(tweet));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Tweet not found", "40401");
        }
        repository.deleteById(id);
    }

    private void validate(TweetRequestTo dto) {
        if (dto.title == null || dto.title.isBlank() ||
            dto.content == null || dto.content.isBlank() ||
            dto.authorId == null) {
            throw new BadRequestException("Invalid fields", "40001");
        }

        // Additional validation rules
        if (dto.title.length() < 2 || dto.title.length() > 32) {
            throw new BadRequestException("Invalid title length", "40001");
        }

        if (dto.content.length() < 4 || dto.content.length() > 2048) {
            throw new BadRequestException("Invalid content length", "40001");
        }
    }

    private TweetResponseTo toResponse(Tweet tweet) {
        TweetResponseTo dto = new TweetResponseTo();
        dto.id = tweet.getId();
        dto.title = tweet.getTitle();
        dto.content = tweet.getContent();
        dto.authorId = tweet.getAuthorId();
        return dto;
    }
}
