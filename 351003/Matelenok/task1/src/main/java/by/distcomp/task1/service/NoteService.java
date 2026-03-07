package by.distcomp.task1.service;

import by.distcomp.task1.dto.NoteRequestTo;
import by.distcomp.task1.dto.NoteResponseTo;
import by.distcomp.task1.exception.ResourceNotFoundException;
import by.distcomp.task1.mapper.NoteMapper;
import by.distcomp.task1.model.Article;
import by.distcomp.task1.model.Note;
import by.distcomp.task1.repository.ArticleRepository;
import by.distcomp.task1.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final ArticleRepository articleRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository,ArticleRepository articleRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.articleRepository=articleRepository;
        this.noteMapper = noteMapper;
    }
    public NoteResponseTo createNote(NoteRequestTo dto) {
        Article article = articleRepository.findById(dto.articleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", dto.articleId()));

        Note note = noteMapper.toEntity(dto);
        note.setArticle(article);
        article.getNotes().add(note);
        Note saved = noteRepository.save(note);
        return noteMapper.toResponse(saved);
    }
    public NoteResponseTo updateNote(Long id, NoteRequestTo dto) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Note not found"));

        if (dto.content() != null) {
            note.setContent(dto.content());
        }

        Note saved = noteRepository.save(note);
        return noteMapper.toResponse(saved);
    }
    public void deleteNote(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note", "id", id));

        Article article = note.getArticle();
        if (article != null) {
            article.removeNote(note);
            articleRepository.save(article);
        }

        noteRepository.deleteById(id);
    }
    public List<NoteResponseTo> getAllNotes() {
        return noteRepository.findAll()
                .stream()
                .map(noteMapper::toResponse)
                .collect(Collectors.toList());
    }
    public NoteResponseTo getNoteById(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Note not found: " + id));

        return noteMapper.toResponse(note);
    }
}
