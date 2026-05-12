package by.shaminko.distcomp.services;

import by.shaminko.distcomp.dto.EditorMapper;
import by.shaminko.distcomp.dto.EditorRequestTo;
import by.shaminko.distcomp.dto.EditorResponseTo;
import by.shaminko.distcomp.entities.UserRole;
import by.shaminko.distcomp.repositories.EditorRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EditorService {
    public final EditorRepository repImpl;
    @Qualifier("editorMapper")
    public final EditorMapper mapper;
    private final PasswordEncoder passwordEncoder;


    public List<EditorResponseTo> getAll() {
        return repImpl.getAll().map(mapper::out).toList();
    }

    @Cacheable(value = "editors", key = "#id")
    public EditorResponseTo getById(Long id) {
        return repImpl.get(id).map(mapper::out).orElseThrow();
    }

    @CachePut(value = "editors", key = "#req.id")
    public EditorResponseTo create(EditorRequestTo req) {
        var entity = mapper.in(req);
        entity.setRole(entity.getRole() == null ? UserRole.CUSTOMER : entity.getRole());
        entity.setPassword(encodeIfNeeded(entity.getPassword()));
        return repImpl.create(entity).map(mapper::out).orElseThrow();
    }

    @CachePut(value = "editors", key = "#req.id")
    public EditorResponseTo update(EditorRequestTo req) {
        var existing = repImpl.findById(req.getId()).orElseThrow();
        var entity = mapper.in(req);
        entity.setRole(entity.getRole() == null ? existing.getRole() : entity.getRole());
        if (entity.getPassword() == null || entity.getPassword().isBlank()) {
            entity.setPassword(existing.getPassword());
        } else {
            entity.setPassword(encodeIfNeeded(entity.getPassword()));
        }
        return repImpl.update(entity).map(mapper::out).orElseThrow();
    }

    @CacheEvict(value = "editors", key = "#id")
    public void delete(Long id) {
        repImpl.delete(id);
    }

    private String encodeIfNeeded(String rawOrEncodedPassword) {
        if (rawOrEncodedPassword == null || rawOrEncodedPassword.isBlank()) {
            return rawOrEncodedPassword;
        }
        if (rawOrEncodedPassword.startsWith("$2")) {
            return rawOrEncodedPassword;
        }
        return passwordEncoder.encode(rawOrEncodedPassword);
    }
}
