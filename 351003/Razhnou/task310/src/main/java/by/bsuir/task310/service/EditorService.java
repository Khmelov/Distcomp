package by.bsuir.task310.service;

import by.bsuir.task310.dto.request.EditorRequestTo;
import by.bsuir.task310.dto.response.EditorResponseTo;
import by.bsuir.task310.entity.Editor;
import by.bsuir.task310.entity.Story;
import by.bsuir.task310.exception.BadRequestException;
import by.bsuir.task310.exception.NotFoundException;
import by.bsuir.task310.mapper.EditorMapper;
import by.bsuir.task310.repository.EditorRepository;
import by.bsuir.task310.repository.NoteRepository;
import by.bsuir.task310.repository.StoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EditorService {
    private final EditorRepository editorRepository;
    private final StoryRepository storyRepository;
    private final NoteRepository noteRepository;

    public EditorService(EditorRepository editorRepository, StoryRepository storyRepository, NoteRepository noteRepository) {
        this.editorRepository = editorRepository;
        this.storyRepository = storyRepository;
        this.noteRepository = noteRepository;
    }

    public EditorResponseTo create(EditorRequestTo request) {
        if (request.id() != null) {
            throw new BadRequestException("User id must be null on create", 3);
        }
        validateLogin(request.login());
        validateText(request.password(), "User password");
        validateText(request.firstname(), "User firstname");
        validateText(request.lastname(), "User lastname");

        Editor editor = EditorMapper.toEntity(request);
        return EditorMapper.toResponse(editorRepository.save(editor));
    }

    public List<EditorResponseTo> findAll() {
        return editorRepository.findAll().stream()
                .map(EditorMapper::toResponse)
                .toList();
    }

    public EditorResponseTo findById(Long id) {
        validateId(id, "User id");
        return EditorMapper.toResponse(getEditor(id));
    }

    public EditorResponseTo update(EditorRequestTo request) {
        validateId(request.id(), "User id");
        validateLogin(request.login());
        validateText(request.password(), "User password");
        validateText(request.firstname(), "User firstname");
        validateText(request.lastname(), "User lastname");
        getEditor(request.id());

        Editor editor = EditorMapper.toEntity(request);
        return EditorMapper.toResponse(editorRepository.update(editor));
    }

    public void delete(Long id) {
        validateId(id, "User id");
        getEditor(id);

        for (Story story : storyRepository.findByEditorId(id)) {
            noteRepository.deleteByStoryId(story.getId());
            storyRepository.deleteById(story.getId());
        }
        editorRepository.deleteById(id);
    }

    public EditorResponseTo findByStoryId(Long storyId) {
        validateId(storyId, "Tweet id");
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new NotFoundException("Tweet not found", 2));
        return EditorMapper.toResponse(getEditor(story.getEditorId()));
    }

    private Editor getEditor(Long id) {
        return editorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found", 1));
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

    private void validateLogin(String login) {
        validateText(login, "User login");
        int length = login.trim().length();
        if (length < 2 || length > 64) {
            throw new BadRequestException("User login length must be between 2 and 64", 8);
        }
    }
}
