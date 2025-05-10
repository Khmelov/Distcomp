package com.example.demo.service;

import com.example.demo.dto.CreatorResponseTo;
import com.example.demo.dto.NoteRequestTo;
import com.example.demo.dto.NoteResponseTo;
import com.example.demo.mapper.NoteMapper;
import com.example.demo.model.Article;
import com.example.demo.model.Note;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.NoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository repository;
    private final NoteMapper mapper;
    private final ArticleRepository articleRepository;

    public NoteResponseTo createNote(NoteRequestTo request){
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

        Note note = mapper.toEntity(request);
        note.setArticle(article);
        note = repository.save(note);
        return mapper.toDto(note);
    }

    public NoteResponseTo getNodeById(Long id){
        Note note = repository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Note not found"));
        return mapper.toDto(note);
    }
    public void deleteNote(Long id){
        if(!repository.existsById(id)){
            throw new EntityNotFoundException("Note not found");
        }
        repository.deleteById(id);
    }
    public List<NoteResponseTo> getAllNotes(){
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public NoteResponseTo updateNote(NoteRequestTo request){
        Note note = repository.findById(request.getId()).orElseThrow(()->new EntityNotFoundException("Note not found"));
        mapper.updateEntityFromDto(request, note);
        Note upadtedNote = repository.save(note);
        return mapper.toDto(upadtedNote);
    }
}
