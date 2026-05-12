package com.adashkevich.rest.lab.service;

import com.adashkevich.rest.lab.dto.request.EditorRequestTo;
import com.adashkevich.rest.lab.dto.response.EditorResponseTo;
import com.adashkevich.rest.lab.exception.ConflictException;
import com.adashkevich.rest.lab.exception.NotFoundException;
import com.adashkevich.rest.lab.mapper.EditorMapper;
import com.adashkevich.rest.lab.model.Editor;
import com.adashkevich.rest.lab.model.Role;
import com.adashkevich.rest.lab.repository.EditorRepository;
import com.adashkevich.rest.lab.repository.NewsRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EditorService {
    private final EditorRepository repo;
    private final NewsRepository newsRepo;
    private final EditorMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public EditorService(EditorRepository repo, NewsRepository newsRepo, EditorMapper mapper, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.newsRepo = newsRepo;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    public EditorResponseTo create(EditorRequestTo dto) {
        return create(dto, false);
    }

    public EditorResponseTo register(EditorRequestTo dto) {
        return create(dto, true);
    }

    private EditorResponseTo create(EditorRequestTo dto, boolean encodePassword) {
        ensureLoginUnique(dto.login, null);
        Editor entity = mapper.toEntity(dto);
        if (entity.getRole() == null) {
            entity.setRole(Role.CUSTOMER);
        }
        if (encodePassword) {
            entity.setPassword(passwordEncoder.encode(dto.password));
        }
        Editor saved = repo.save(entity);
        return mapper.toResponse(saved);
    }

    public List<EditorResponseTo> getAll() {
        return repo.findAll().stream().map(mapper::toResponse).toList();
    }

    public EditorResponseTo getById(Long id) {
        Editor e = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Editor not found", "40410"));
        return mapper.toResponse(e);
    }

    public Optional<Editor> findByLogin(String login) {
        return repo.findAll().stream()
                .filter(editor -> editor.getLogin().equalsIgnoreCase(login))
                .findFirst();
    }

    public EditorResponseTo update(Long id, EditorRequestTo dto) {
        Editor existing = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Editor not found", "40410"));

        ensureLoginUnique(dto.login, id);

        existing.setLogin(dto.login);
        existing.setPassword(dto.password);
        existing.setFirstname(dto.firstname);
        existing.setLastname(dto.lastname);
        existing.setRole(dto.role == null ? Role.CUSTOMER : dto.role);

        Editor updated = repo.update(id, existing);
        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        Editor editor = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Editor not found", "40410"));

        boolean hasLinkedNews = newsRepo.findAll().stream()
                .anyMatch(news -> id.equals(news.getEditorId()));

        if (hasLinkedNews) {
            throw new ConflictException("Editor is used by news", "40911");
        }

        repo.deleteById(editor.getId());
    }

    private void ensureLoginUnique(String login, Long selfId) {
        boolean exists = repo.findAll().stream()
                .anyMatch(e -> e.getLogin().equalsIgnoreCase(login)
                        && (selfId == null || !e.getId().equals(selfId)));

        if (exists) {
            throw new ConflictException("Editor login must be unique", "40910");
        }
    }
}
