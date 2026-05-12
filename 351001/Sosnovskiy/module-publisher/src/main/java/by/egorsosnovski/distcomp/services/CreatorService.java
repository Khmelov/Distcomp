package by.egorsosnovski.distcomp.services;

import by.egorsosnovski.distcomp.dto.CreatorMapper;
import by.egorsosnovski.distcomp.dto.CreatorRequestTo;
import by.egorsosnovski.distcomp.dto.CreatorResponseTo;
import by.egorsosnovski.distcomp.repositories.CreatorRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CreatorService {
    public final CreatorRepository repImpl;
    @Qualifier("creatorMapper")
    public final CreatorMapper mapper;


    public List<CreatorResponseTo> getAll() {
        return repImpl.getAll().map(mapper::out).toList();
    }
    @Cacheable(value = "creators", key = "#id")
    public CreatorResponseTo getById(Long id) {
        return repImpl.get(id).map(mapper::out).orElseThrow();
    }
    @CachePut(value = "creators", key = "#req.id")
    public CreatorResponseTo create(CreatorRequestTo req) {
        return repImpl.create(mapper.in(req)).map(mapper::out).orElseThrow();
    }
    @CachePut(value = "creators", key = "#req.id")
    public CreatorResponseTo update(CreatorRequestTo req) {
        return repImpl.update(mapper.in(req)).map(mapper::out).orElseThrow();
    }
    @CacheEvict(value = "creators", key = "#id")
    public void delete(Long id) {
        repImpl.delete(id);
    }
}
