package by.kopvzakone.distcomp.services;

import by.kopvzakone.distcomp.dto.*;
import by.kopvzakone.distcomp.repositories.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TagService {
    public final TagRepository repImpl;
    @Qualifier("tagMapper")
    public final TagMapper mapper;

    public List<TagResponseTo> getAll() {
        return repImpl.getAll().map(mapper::out).toList();
    }
    public TagResponseTo getById(Long id) {
        return repImpl.get(id).map(mapper::out).orElseThrow();
    }
    public TagResponseTo create(TagRequestTo req) {
        return repImpl.create(mapper.in(req)).map(mapper::out).orElseThrow();
    }
    public TagResponseTo update(TagRequestTo req) {
        return repImpl.update(mapper.in(req)).map(mapper::out).orElseThrow();
    }
    public void delete(Long id) {
        repImpl.delete(id);
    }
}
