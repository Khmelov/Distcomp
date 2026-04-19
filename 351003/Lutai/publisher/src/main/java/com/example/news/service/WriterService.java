package com.example.news.service;

import com.example.common.dto.WriterRequestTo;
import com.example.common.dto.WriterResponseTo;
import com.example.news.entity.Writer;
import com.example.common.exception.EntityNotFoundException;
import com.example.common.exception.LoginAlreadyExistsException;
import com.example.news.mapper.WriterMapper;
import com.example.news.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WriterService {

    private final WriterRepository writerRepository;
    private final WriterMapper writerMapper;

    @Transactional
    public WriterResponseTo create(WriterRequestTo request) {
        if (writerRepository.existsByLogin(request.login())) {
            throw new LoginAlreadyExistsException("Writer with login " + request.login() + " already exists");
        }

        Writer writer = writerMapper.toEntity(request);
        Writer saved = writerRepository.save(writer);
        return writerMapper.toResponse(saved);
    }

    public List<WriterResponseTo> findAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return writerRepository.findAll(pageable).getContent().stream()
                .map(writerMapper::toResponse)
                .collect(Collectors.toList());
    }

    public WriterResponseTo findById(Long id) {
        return writerRepository.findById(id)
                .map(writerMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Writer not found", "40401"));
    }

    @Transactional
    public WriterResponseTo update(Long id, WriterRequestTo request) {
        Writer existingWriter = writerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Writer not found", "40401"));

        existingWriter.setLogin(request.login());
        existingWriter.setFirstname(request.firstname());
        existingWriter.setLastname(request.lastname());
        existingWriter.setPassword(request.password());

        return writerMapper.toResponse(writerRepository.save(existingWriter));
    }

    @Transactional
    public void delete(Long id) {
        if (!writerRepository.existsById(id)) {
            throw new EntityNotFoundException("Writer not found", "40401");
        }
        writerRepository.deleteById(id);
    }

}