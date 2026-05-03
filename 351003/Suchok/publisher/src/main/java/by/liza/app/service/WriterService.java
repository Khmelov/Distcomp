package by.liza.app.service;

import by.liza.app.dto.request.WriterRequestTo;
import by.liza.app.dto.response.WriterResponseTo;
import by.liza.app.exception.DuplicateEntityException;
import by.liza.app.exception.EntityNotFoundException;
import by.liza.app.mapper.WriterMapper;
import by.liza.app.model.Writer;
import by.liza.app.repository.ArticleRepository;
import by.liza.app.repository.MarkRepository;
import by.liza.app.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WriterService {

    private final WriterRepository  writerRepository;
    private final ArticleRepository articleRepository;
    private final MarkRepository    markRepository;
    private final WriterMapper      writerMapper;
    private final PasswordEncoder   passwordEncoder;

    @Transactional
    @Caching(
            put  = @CachePut(value = "writers", key = "#result.id"),
            evict = @CacheEvict(value = "writers_all", allEntries = true)
    )
    public WriterResponseTo create(WriterRequestTo requestTo) {
        if (writerRepository.existsByLogin(requestTo.getLogin())) {
            throw new DuplicateEntityException(
                    "Writer with login '" + requestTo.getLogin() + "' already exists", 40301);
        }
        Writer writer = writerMapper.toEntity(requestTo);
        writer.setPassword(passwordEncoder.encode(requestTo.getPassword()));
        writer.setRole(requestTo.getRole() != null
                ? Writer.Role.valueOf(requestTo.getRole().toUpperCase())
                : Writer.Role.CUSTOMER);
        return writerMapper.toResponse(writerRepository.save(writer));
    }

    @Cacheable(value = "writers", key = "#id")
    public WriterResponseTo getById(Long id) {
        return writerMapper.toResponse(findById(id));
    }

    @Cacheable(value = "writers_all")
    public List<WriterResponseTo> getAll() {
        return writerMapper.toResponseList(writerRepository.findAll());
    }

    @Transactional
    @Caching(
            put  = @CachePut(value = "writers", key = "#result.id"),
            evict = @CacheEvict(value = "writers_all", allEntries = true)
    )
    public WriterResponseTo update(WriterRequestTo requestTo) {
        if (requestTo.getId() == null) {
            throw new EntityNotFoundException("Writer id must be provided for update", 40001);
        }
        Writer existing = findById(requestTo.getId());
        if (writerRepository.existsByLoginAndIdNot(requestTo.getLogin(), requestTo.getId())) {
            throw new DuplicateEntityException(
                    "Writer with login '" + requestTo.getLogin() + "' already exists", 40301);
        }
        writerMapper.updateEntityFromRequest(requestTo, existing);
        existing.setPassword(passwordEncoder.encode(requestTo.getPassword()));
        if (requestTo.getRole() != null) {
            existing.setRole(Writer.Role.valueOf(requestTo.getRole().toUpperCase()));
        }
        return writerMapper.toResponse(writerRepository.save(existing));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "writers",     key = "#id"),
            @CacheEvict(value = "writers_all", allEntries = true)
    })
    public void delete(Long id) {
        if (!writerRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Writer with id " + id + " not found", 40401);
        }
        writerRepository.deleteById(id);
    }

    private Writer findById(Long id) {
        return writerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Writer with id " + id + " not found", 40401));
    }
}
