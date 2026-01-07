package com.group310971.gormash.service;

import com.group310971.gormash.dto.EditorRequestTo;
import com.group310971.gormash.dto.EditorResponseTo;
import com.group310971.gormash.mapper.EditorMapper;
import com.group310971.gormash.model.Editor;
import com.group310971.gormash.repository.EditorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EditorService {
    private final EditorRepository editorRepository;
    private final EditorMapper editorMapper = EditorMapper.INSTANCE;

    public EditorResponseTo createEditor(@Valid EditorRequestTo editorRequestTo){
        Editor editor = editorMapper.toEntity(editorRequestTo);
        Editor savedEditor = editorRepository.save(editor);
        return editorMapper.toResponse(savedEditor);
    }

    public EditorResponseTo updateEditor(@Valid EditorRequestTo editorRequestTo){
        if (editorRequestTo.getId() == null) {
            throw new RuntimeException("Editor id cannot be null for update");
        }

        var optional = editorRepository.findById(editorRequestTo.getId());
        if (optional.isEmpty())
            throw new RuntimeException("Editor not exists");
        Editor persistence = optional.get();
        Editor editor = editorMapper.toEntity(editorRequestTo);
        persistence.setFirstname(editor.getFirstname());
        persistence.setLastname(editor.getLastname());
        persistence.setLogin(editor.getLogin());
        persistence.setPassword(editor.getPassword());
        Editor updatedEditor = editorRepository.save(persistence);
        return editorMapper.toResponse(updatedEditor);
    }

    public EditorResponseTo getEditorById(Long id) {
        return editorRepository.findById(id)
                .map(editorMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Editor not found with id: " + id));
    }

    public List<EditorResponseTo> getAllEditors(){
        LinkedList<EditorResponseTo> list = new LinkedList<>();
        for (Editor editor : editorRepository.findAll()){
            list.add(editorMapper.toResponse(editor));
        }
        return list;
    }

    public EditorResponseTo deleteEditor(Long id) {
        var optional = editorRepository.findById(id);
        if (optional.isEmpty())
            throw new RuntimeException("Editor not exists");
        Editor editor = optional.get();
        editorRepository.delete(editor);
        return editorMapper.toResponse(editor);
    }
}
