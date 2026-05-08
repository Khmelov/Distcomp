package by.shaminko.distcomp.services;

import by.shaminko.distcomp.dto.EditorMapper;
import by.shaminko.distcomp.dto.EditorRequestTo;
import by.shaminko.distcomp.dto.EditorResponseTo;
import by.shaminko.distcomp.repositories.EditorRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "editors", key = "#id")
    public EditorResponseTo getById(Long id) {
        return repImpl.get(id).map(mapper::out).orElseThrow();
    }

    @CachePut(value = "editors", key = "#req.id")
    public EditorResponseTo create(EditorRequestTo req) {
        return repImpl.create(mapper.in(req)).map(mapper::out).orElseThrow();
    }

    @CachePut(value = "editors", key = "#req.id")
    public EditorResponseTo update(EditorRequestTo req) {
        return repImpl.update(mapper.in(req)).map(mapper::out).orElseThrow();
    }

    @CacheEvict(value = "editors", key = "#id")
    public void delete(Long id) {
        repImpl.delete(id);
    }
}
