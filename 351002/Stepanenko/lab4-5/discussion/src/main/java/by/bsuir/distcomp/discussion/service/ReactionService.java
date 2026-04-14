package by.bsuir.distcomp.discussion.service;

import by.bsuir.distcomp.discussion.domain.Reaction;
import by.bsuir.distcomp.discussion.domain.ReactionById;
import by.bsuir.distcomp.discussion.domain.ReactionKey;
import by.bsuir.distcomp.discussion.repository.ReactionByIdRepository;
import by.bsuir.distcomp.discussion.repository.ReactionRepository;
import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.reaction.ReactionState;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.discussion.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final ReactionByIdRepository reactionByIdRepository;

    public ReactionService(ReactionRepository reactionRepository,
                           ReactionByIdRepository reactionByIdRepository) {
        this.reactionRepository = reactionRepository;
        this.reactionByIdRepository = reactionByIdRepository;
    }

    // ЭТОТ МЕТОД ИСКАЛ КОНТРОЛЛЕР
    public List<ReactionResponseTo> getByTweetId(Long tweetId) {
        return reactionRepository.findByKeyTweetId(tweetId).stream()
                .map(r -> new ReactionResponseTo(
                        r.getKey().getId(),
                        r.getKey().getTweetId(),
                        r.getContent(),
                        r.getState()))
                .collect(Collectors.toList());
    }

    public ReactionResponseTo create(ReactionRequestTo dto) {
        ReactionState state = moderate(dto.getContent());

        ReactionKey key = new ReactionKey();
        key.setTweetId(dto.getTweetId());
        key.setId(dto.getId());

        Reaction row = new Reaction();
        row.setKey(key);
        row.setContent(dto.getContent());
        row.setState(state);
        reactionRepository.save(row);

        ReactionById lookup = new ReactionById();
        lookup.setId(dto.getId());
        lookup.setTweetId(dto.getTweetId());
        lookup.setContent(dto.getContent());
        lookup.setState(state);
        reactionByIdRepository.save(lookup);

        return new ReactionResponseTo(dto.getId(), dto.getTweetId(), dto.getContent(), state);
    }

    public ReactionResponseTo getById(Long id) {
        ReactionById found = reactionByIdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found", 40417));
        return new ReactionResponseTo(found.getId(), found.getTweetId(), found.getContent(), found.getState());
    }

    public List<ReactionResponseTo> getAll() {
        return reactionByIdRepository.findAll().stream()
                .map(r -> new ReactionResponseTo(r.getId(), r.getTweetId(), r.getContent(), r.getState()))
                .collect(Collectors.toList());
    }

    public ReactionResponseTo update(ReactionRequestTo dto) {
        if (!reactionByIdRepository.existsById(dto.getId())) {
            throw new ResourceNotFoundException("Reaction not found", 40418);
        }
        return create(dto);
    }

    public void deleteById(Long id) {
        ReactionById existing = reactionByIdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found", 40420));

        ReactionKey key = new ReactionKey();
        key.setTweetId(existing.getTweetId());
        key.setId(id);

        reactionRepository.deleteById(key);
        reactionByIdRepository.deleteById(id);
    }

    public void deleteByTweetId(Long tweetId) {
        List<Reaction> rows = reactionRepository.findByKeyTweetId(tweetId);
        for (Reaction r : rows) {
            reactionByIdRepository.deleteById(r.getKey().getId());
        }
        reactionRepository.deleteAll(rows);
    }

    private ReactionState moderate(String content) {
        if (content != null && (content.toLowerCase().contains("spam") || content.toLowerCase().contains("scam"))) {
            return ReactionState.DECLINE;
        }
        return ReactionState.APPROVE;
    }
}