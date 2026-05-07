package by.tracker.rest_api.service;

import by.tracker.rest_api.dto.TweetRequestTo;
import by.tracker.rest_api.dto.TweetResponseTo;
import by.tracker.rest_api.model.Tweet;
import by.tracker.rest_api.repository.CrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TweetService {

    private final CrudRepository<Tweet, Long> repository;
    private final TweetMapper mapper;

    public TweetResponseTo create(TweetRequestTo request) {
        Tweet entity = mapper.toEntity(request);
        Tweet saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public List<TweetResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public TweetResponseTo getById(Long id) {
        Tweet entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tweet not found with id: " + id));
        return mapper.toResponse(entity);
    }

    public TweetResponseTo update(TweetRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("ID is required for update");
        }
        Tweet entity = mapper.toEntity(request);
        entity.setId(request.getId());
        Tweet updated = repository.update(entity);
        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}