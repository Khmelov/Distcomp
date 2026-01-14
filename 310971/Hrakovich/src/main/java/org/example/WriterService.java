package org.example;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class WriterService {

    private final WriterRepository writerRepository;
    private final WriterMapper writerMapper;

    public WriterService(
            WriterRepository writerRepository,
            WriterMapper writerMapper
    ) {
        this.writerRepository = writerRepository;
        this.writerMapper = writerMapper;
    }

    // ---------- CREATE ----------
    public WriterResponseTo create(WriterRequestTo dto) {
        Writer writer = writerMapper.toEntity(dto);
        writer.setPassword(dto.getPassword());

        return writerMapper.toResponse(
                writerRepository.save(writer)
        );
    }

    // ---------- READ ----------
    public WriterResponseTo getById(Long id) {
        return writerMapper.toResponse(
                writerRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Writer not found"))
        );
    }

    public List<WriterResponseTo> getAll() {
        return writerRepository.findAll(
                        org.springframework.data.domain.Sort.by("id")
                ).stream()
                .map(writerMapper::toResponse)
                .toList();
    }

    // ---------- UPDATE ----------
    public WriterResponseTo update(Long id, WriterRequestTo dto) {
        Writer writer = writerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Writer not found"));

        writer.setLogin(dto.getLogin());
        writer.setPassword(dto.getPassword());
        writer.setFirstname(dto.getFirstname());
        writer.setLastname(dto.getLastname());

        return writerMapper.toResponse(writer);
    }

    // ---------- DELETE ----------
    public void delete(Long id) {
        if (!writerRepository.existsById(id)) {
            throw new EntityNotFoundException("Writer not found");
        }
        writerRepository.deleteById(id);
    }
}