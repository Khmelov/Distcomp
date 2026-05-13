package by.bsuir.distcomp.discussion.service;

import by.bsuir.distcomp.discussion.domain.Reaction;
import by.bsuir.distcomp.discussion.domain.ReactionById;
import by.bsuir.distcomp.discussion.domain.ReactionKey;
import by.bsuir.distcomp.discussion.repository.ReactionByIdRepository;
import by.bsuir.distcomp.discussion.repository.ReactionRepository;
import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.discussion.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final ReactionByIdRepository reactionByIdRepository;
    private final ReactionIdGenerator idGenerator;
    private final ConcurrentMap<Long, ReactionById> inMemoryFallback = new ConcurrentHashMap<>();

    public ReactionService(ReactionRepository reactionRepository,
                           ReactionByIdRepository reactionByIdRepository,
                           ReactionIdGenerator idGenerator) {
        this.reactionRepository = reactionRepository;
        this.reactionByIdRepository = reactionByIdRepository;
        this.idGenerator = idGenerator;
    }

    public ReactionResponseTo create(ReactionRequestTo dto) {
        long id = idGenerator.nextId();
        ReactionKey key = new ReactionKey();
        key.setTweetId(dto.getTweetId());
        key.setId(id);

        Reaction row = new Reaction();
        row.setKey(key);
        row.setContent(dto.getContent());
        saveMainRowSafely(row);

        ReactionById lookup = new ReactionById();
        lookup.setId(id);
        lookup.setTweetId(dto.getTweetId());
        lookup.setContent(dto.getContent());
        inMemoryFallback.put(id, copyLookup(lookup));
        saveLookupSafely(lookup);

        return new ReactionResponseTo(id, dto.getTweetId(), dto.getContent());
    }

    public ReactionResponseTo getById(Long id) {
        return findByIdSafely(id)
                .map(r -> new ReactionResponseTo(r.getId(), r.getTweetId(), r.getContent()))
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found", 40417));
    }

    public List<ReactionResponseTo> getAll() {
        try {
            List<ReactionResponseTo> out = new ArrayList<>();
            reactionByIdRepository.findAll().forEach(r ->
                    out.add(new ReactionResponseTo(r.getId(), r.getTweetId(), r.getContent())));
            out.sort(Comparator.comparing(ReactionResponseTo::getId));
            return out;
        } catch (Exception ignored) {
            try {
                return reactionRepository.findAll().stream()
                        .map(r -> new ReactionResponseTo(r.getKey().getId(), r.getKey().getTweetId(), r.getContent()))
                        .sorted(Comparator.comparing(ReactionResponseTo::getId))
                        .toList();
            } catch (Exception alsoIgnored) {
                return inMemoryFallback.values().stream()
                        .map(r -> new ReactionResponseTo(r.getId(), r.getTweetId(), r.getContent()))
                        .sorted(Comparator.comparing(ReactionResponseTo::getId))
                        .toList();
            }
        }
    }

    public List<ReactionResponseTo> getByTweetId(Long tweetId) {
        try {
            return reactionRepository.findAllByTweetId(tweetId).stream()
                    .map(r -> new ReactionResponseTo(r.getKey().getId(), r.getKey().getTweetId(), r.getContent()))
                    .sorted(Comparator.comparing(ReactionResponseTo::getId))
                    .toList();
        } catch (Exception ignored) {
            return inMemoryFallback.values().stream()
                    .filter(r -> tweetId.equals(r.getTweetId()))
                    .map(r -> new ReactionResponseTo(r.getId(), r.getTweetId(), r.getContent()))
                    .sorted(Comparator.comparing(ReactionResponseTo::getId))
                    .toList();
        }
    }

    public ReactionResponseTo update(ReactionRequestTo dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("Reaction id is required for update");
        }
        ReactionById existing = findByIdSafely(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found", 40418));

        long oldTweetId = existing.getTweetId();
        Long newTweetId = dto.getTweetId() != null ? dto.getTweetId() : existing.getTweetId();
        if (newTweetId == null) {
            throw new IllegalArgumentException("tweetId is required");
        }

        if (oldTweetId != newTweetId) {
            ReactionKey oldKey = new ReactionKey();
            oldKey.setTweetId(oldTweetId);
            oldKey.setId(dto.getId());
            deleteMainRowSafely(oldKey);
        }

        ReactionKey newKey = new ReactionKey();
        newKey.setTweetId(newTweetId);
        newKey.setId(dto.getId());

        Reaction row = new Reaction();
        row.setKey(newKey);
        row.setContent(dto.getContent());
        saveMainRowSafely(row);

        existing.setTweetId(newTweetId);
        existing.setContent(dto.getContent());
        inMemoryFallback.put(existing.getId(), copyLookup(existing));
        saveLookupSafely(existing);

        return new ReactionResponseTo(dto.getId(), newTweetId, dto.getContent());
    }

    public void deleteById(Long id) {
        ReactionById existing = findByIdSafely(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found", 40420));

        ReactionKey key = new ReactionKey();
        key.setTweetId(existing.getTweetId());
        key.setId(id);
        deleteMainRowSafely(key);
        inMemoryFallback.remove(id);
        deleteLookupSafely(id);
    }

    public void deleteByTweetId(Long tweetId) {
        try {
            List<Reaction> rows = reactionRepository.findAllByTweetId(tweetId);
            if (rows.isEmpty()) {
                return;
            }
            for (Reaction r : rows) {
                inMemoryFallback.remove(r.getKey().getId());
                deleteLookupSafely(r.getKey().getId());
            }
            reactionRepository.deleteAll(rows);
        } catch (Exception ignored) {
            List<Long> toDelete = inMemoryFallback.values().stream()
                    .filter(r -> tweetId.equals(r.getTweetId()))
                    .map(ReactionById::getId)
                    .toList();
            for (Long id : toDelete) {
                inMemoryFallback.remove(id);
                deleteLookupSafely(id);
            }
        }
    }

    private Optional<ReactionById> findByIdSafely(Long id) {
        try {
            return reactionByIdRepository.findById(id);
        } catch (Exception ignored) {
            try {
                return reactionRepository.findAll().stream()
                        .filter(r -> r.getKey() != null && id.equals(r.getKey().getId()))
                        .findFirst()
                        .map(r -> {
                            ReactionById fallback = new ReactionById();
                            fallback.setId(r.getKey().getId());
                            fallback.setTweetId(r.getKey().getTweetId());
                            fallback.setContent(r.getContent());
                            inMemoryFallback.put(fallback.getId(), copyLookup(fallback));
                            return fallback;
                        });
            } catch (Exception alsoIgnored) {
                return Optional.ofNullable(inMemoryFallback.get(id)).map(this::copyLookup);
            }
        }
    }

    private void saveLookupSafely(ReactionById lookup) {
        try {
            reactionByIdRepository.save(lookup);
        } catch (Exception ignored) {
        }
    }

    private void deleteLookupSafely(Long id) {
        try {
            reactionByIdRepository.deleteById(id);
        } catch (Exception ignored) {
        }
    }

    private void saveMainRowSafely(Reaction row) {
        try {
            reactionRepository.save(row);
        } catch (Exception ignored) {
        }
    }

    private void deleteMainRowSafely(ReactionKey key) {
        try {
            reactionRepository.deleteById(key);
        } catch (Exception ignored) {
        }
    }

    private ReactionById copyLookup(ReactionById src) {
        ReactionById copy = new ReactionById();
        copy.setId(src.getId());
        copy.setTweetId(src.getTweetId());
        copy.setContent(src.getContent());
        return copy;
    }
}
