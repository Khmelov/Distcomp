package by.bsuir.task310.service;

import by.bsuir.task310.dto.request.StoryRequestTo;
import by.bsuir.task310.dto.response.StoryResponseTo;
import by.bsuir.task310.entity.Story;
import by.bsuir.task310.exception.BadRequestException;
import by.bsuir.task310.exception.NotFoundException;
import by.bsuir.task310.mapper.StoryMapper;
import by.bsuir.task310.repository.EditorRepository;
import by.bsuir.task310.repository.NoteRepository;
import by.bsuir.task310.repository.StoryRepository;
import by.bsuir.task310.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class StoryService {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final StoryRepository storyRepository;
    private final EditorRepository editorRepository;
    private final TagRepository tagRepository;
    private final NoteRepository noteRepository;

    public StoryService(StoryRepository storyRepository, EditorRepository editorRepository, TagRepository tagRepository,
                        NoteRepository noteRepository) {
        this.storyRepository = storyRepository;
        this.editorRepository = editorRepository;
        this.tagRepository = tagRepository;
        this.noteRepository = noteRepository;
    }

    public StoryResponseTo create(StoryRequestTo request) {
        if (request.id() != null) {
            throw new BadRequestException("Tweet id must be null on create", 3);
        }

        validateId(request.editorId(), "User id");
        requireEditorExists(request.editorId());
        validateTitle(request.title());
        validateText(request.content(), "Tweet content");
        List<Long> tagIds = validateAndNormalizeTagIds(request.tagIds());

        Story story = StoryMapper.toEntity(request);
        String now = currentTimestamp();
        story.setCreated(now);
        story.setModified(now);
        story.setTagIds(tagIds);

        return StoryMapper.toResponse(storyRepository.save(story));
    }

    public List<StoryResponseTo> findAll() {
        return storyRepository.findAll().stream()
                .map(StoryMapper::toResponse)
                .toList();
    }

    public StoryResponseTo findById(Long id) {
        validateId(id, "Tweet id");
        return StoryMapper.toResponse(getStory(id));
    }

    public StoryResponseTo update(StoryRequestTo request) {
        validateId(request.id(), "Tweet id");
        validateId(request.editorId(), "User id");
        requireEditorExists(request.editorId());
        validateTitle(request.title());
        validateText(request.content(), "Tweet content");
        List<Long> tagIds = validateAndNormalizeTagIds(request.tagIds());

        Story existing = getStory(request.id());
        Story story = StoryMapper.toEntity(request);
        story.setCreated(existing.getCreated());
        story.setModified(currentTimestamp());
        story.setTagIds(tagIds);

        return StoryMapper.toResponse(storyRepository.update(story));
    }

    public void delete(Long id) {
        validateId(id, "Tweet id");
        getStory(id);
        noteRepository.deleteByStoryId(id);
        storyRepository.deleteById(id);
    }

    public Story getStory(Long id) {
        return storyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found", 2));
    }

    private List<Long> validateAndNormalizeTagIds(List<Long> tagIds) {
        List<Long> normalized = tagIds == null ? new ArrayList<>() : new ArrayList<>(tagIds);
        for (Long tagId : normalized) {
            validateId(tagId, "Tag id");
            if (!tagRepository.existsById(tagId)) {
                throw new BadRequestException("Tag with id " + tagId + " does not exist", 6);
            }
        }
        return normalized;
    }

    private void requireEditorExists(Long editorId) {
        if (!editorRepository.existsById(editorId)) {
            throw new BadRequestException("User with id " + editorId + " does not exist", 4);
        }
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BadRequestException(fieldName + " must be greater than 0", 1);
        }
    }

    private void validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " must not be blank", 2);
        }
    }

    private void validateTitle(String title) {
        validateText(title, "Tweet title");
        int length = title.trim().length();
        if (length < 2 || length > 64) {
            throw new BadRequestException("Tweet title length must be between 2 and 64", 8);
        }
    }

    private String currentTimestamp() {
        return LocalDateTime.now()
                .truncatedTo(ChronoUnit.SECONDS)
                .format(TIMESTAMP_FORMATTER);
    }
}
