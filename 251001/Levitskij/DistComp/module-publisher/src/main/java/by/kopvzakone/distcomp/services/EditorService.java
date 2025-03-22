package by.kopvzakone.distcomp.services;

import by.kopvzakone.distcomp.dto.EditorMapper;
import by.kopvzakone.distcomp.dto.EditorRequestTo;
import by.kopvzakone.distcomp.dto.EditorResponseTo;
import by.kopvzakone.distcomp.repositories.EditorRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EditorService {
    public final EditorRepository repImpl;
    @Qualifier("editorMapper")
    public final EditorMapper mapper;

    public List<EditorResponseTo> getAll() {
        return repImpl.getAll().map(mapper::out).toList();
    }
    public EditorResponseTo getById(Long id) {
        return repImpl.get(id).map(mapper::out).orElseThrow();
    }
    public EditorResponseTo create(EditorRequestTo req) {
        return repImpl.create(mapper.in(req)).map(mapper::out).orElseThrow();
    }
    public EditorResponseTo update(EditorRequestTo req) {
        return repImpl.update(mapper.in(req)).map(mapper::out).orElseThrow();
    }
    public void delete(Long id) {
        repImpl.delete(id);
    }
}
