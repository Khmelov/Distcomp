package by.bsuir.task310.service;

import by.bsuir.task310.dto.ReactionRequestTo;
import by.bsuir.task310.dto.ReactionResponseTo;
import by.bsuir.task310.exception.EntityNotFoundException;
import by.bsuir.task310.mapper.ReactionMapper;
import by.bsuir.task310.model.Reaction;
import by.bsuir.task310.repository.ReactionRepository;
import by.bsuir.task310.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReactionService {

    private final ReactionRepository repository;
    private final TopicRepository topicRepository;
    private final ReactionMapper mapper;

    public ReactionService(ReactionRepository repository, TopicRepository topicRepository, ReactionMapper mapper) {
        this.repository = repository;
        this.topicRepository = topicRepository;
        this.mapper = mapper;
    }

    public ReactionResponseTo create(ReactionRequestTo requestTo) {
        if (!topicRepository.existsById(requestTo.getTopicId())) {
            throw new EntityNotFoundException("Topic not found");
        }

        Reaction reaction = mapper.toEntity(requestTo);
        return mapper.toResponseTo(repository.save(reaction));
    }

    public List<ReactionResponseTo> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseTo)
                .toList();
    }

    public ReactionResponseTo getById(Long id) {
        Reaction reaction = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found"));

        return mapper.toResponseTo(reaction);
    }

    public ReactionResponseTo update(ReactionRequestTo requestTo) {
        if (!repository.existsById(requestTo.getId())) {
            throw new EntityNotFoundException("Reaction not found");
        }

        if (!topicRepository.existsById(requestTo.getTopicId())) {
            throw new EntityNotFoundException("Topic not found");
        }

        Reaction reaction = mapper.toEntity(requestTo);
        return mapper.toResponseTo(repository.save(reaction));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Reaction not found");
        }

        repository.deleteById(id);
    }
}