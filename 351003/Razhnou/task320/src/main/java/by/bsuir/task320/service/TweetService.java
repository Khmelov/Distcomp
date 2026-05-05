package by.bsuir.task320.service;

import by.bsuir.task320.dto.request.TweetRequestTo;
import by.bsuir.task320.dto.response.TweetResponseTo;
import by.bsuir.task320.entity.Tag;
import by.bsuir.task320.entity.Tweet;
import by.bsuir.task320.entity.User;
import by.bsuir.task320.exception.BadRequestException;
import by.bsuir.task320.exception.ConflictException;
import by.bsuir.task320.exception.NotFoundException;
import by.bsuir.task320.mapper.TweetMapper;
import by.bsuir.task320.repository.TagRepository;
import by.bsuir.task320.repository.TweetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TweetService {
    private final TweetRepository tweetRepository;
    private final TagRepository tagRepository;
    private final UserService userService;
    private final TagService tagService;

    public TweetService(TweetRepository tweetRepository, TagRepository tagRepository, UserService userService, TagService tagService) {
        this.tweetRepository = tweetRepository;
        this.tagRepository = tagRepository;
        this.userService = userService;
        this.tagService = tagService;
    }

    @Transactional
    public TweetResponseTo create(TweetRequestTo request) {
        if (request.id() != null) {
            throw new BadRequestException("Tweet id must be null on create", 3);
        }
        validateId(request.userId(), "User id");
        validateTitle(request.title());
        validateContent(request.content());
        validateUniqueTitleOnCreate(request.title());

        User user = userService.getUser(request.userId());
        Set<Tag> tags = tagService.resolveTags(request.tagIds(), request.tags());
        LocalDateTime now = now();
        Tweet tweet = TweetMapper.toEntity(user, request.title(), request.content(), now, now, tags);
        return TweetMapper.toResponse(tweetRepository.save(tweet));
    }

    @Transactional(readOnly = true)
    public List<TweetResponseTo> findAll() {
        return tweetRepository.findAll().stream()
                .map(TweetMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TweetResponseTo findById(Long id) {
        validateId(id, "Tweet id");
        return TweetMapper.toResponse(getTweet(id));
    }

    @Transactional
    public TweetResponseTo update(TweetRequestTo request) {
        validateId(request.id(), "Tweet id");
        validateId(request.userId(), "User id");
        validateTitle(request.title());
        validateContent(request.content());
        validateUniqueTitleOnUpdate(request.title(), request.id());

        Tweet tweet = getTweet(request.id());
        Set<Tag> currentTags = new LinkedHashSet<>(tweet.getTags());
        User user = userService.getUser(request.userId());
        Set<Tag> tags = request.tagIds() == null && request.tags() == null
                ? tweet.getTags()
                : tagService.resolveTags(request.tagIds(), request.tags());
        TweetMapper.updateEntity(tweet, user, request.title(), request.content(), now(), tags);
        Tweet savedTweet = tweetRepository.saveAndFlush(tweet);

        if (request.tagIds() != null || request.tags() != null) {
            cleanupUnusedTags(findRemovedTags(currentTags, tags));
        }

        return TweetMapper.toResponse(savedTweet);
    }

    @Transactional
    public void delete(Long id) {
        validateId(id, "Tweet id");
        Tweet tweet = getTweet(id);
        Set<Tag> orphanCandidates = new LinkedHashSet<>(tweet.getTags());
        tweetRepository.delete(tweet);
        tweetRepository.flush();
        cleanupUnusedTags(orphanCandidates);
    }

    public Tweet getTweet(Long id) {
        return tweetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found", 2));
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BadRequestException(fieldName + " must be greater than 0", 1);
        }
    }

    private void validateTitle(String title) {
        validateTextLength(title, "Tweet title", 2, 64, 5);
    }

    private void validateContent(String content) {
        validateTextLength(content, "Tweet content", 4, 2048, 6);
    }

    private void validateTextLength(String value, String fieldName, int min, int max, int suffix) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " must not be blank", suffix);
        }
        int length = value.trim().length();
        if (length < min || length > max) {
            throw new BadRequestException(fieldName + " length must be between " + min + " and " + max, suffix);
        }
    }

    private void validateUniqueTitleOnCreate(String title) {
        if (tweetRepository.existsByTitle(title.trim())) {
            throw new ConflictException("Tweet title already exists", 1);
        }
    }

    private void validateUniqueTitleOnUpdate(String title, Long id) {
        if (tweetRepository.existsByTitleAndIdNot(title.trim(), id)) {
            throw new ConflictException("Tweet title already exists", 1);
        }
    }

    private Set<Tag> findRemovedTags(Set<Tag> currentTags, Set<Tag> updatedTags) {
        Set<Long> updatedTagIds = updatedTags.stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());

        return currentTags.stream()
                .filter(tag -> tag.getId() != null && !updatedTagIds.contains(tag.getId()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void cleanupUnusedTags(Set<Tag> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return;
        }

        for (Tag tag : candidates) {
            if (tag.getId() != null && tagRepository.countTweetsUsingTag(tag.getId()) == 0) {
                tagRepository.delete(tag);
            }
        }
        tagRepository.flush();
    }

    private LocalDateTime now() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
