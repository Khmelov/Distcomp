package com.bsuir.romanmuhtasarov.serivces.impl;

import com.bsuir.romanmuhtasarov.domain.entity.Writer;
import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;
import com.bsuir.romanmuhtasarov.domain.mapper.WriterListMapper;
import com.bsuir.romanmuhtasarov.domain.mapper.WriterMapper;
import com.bsuir.romanmuhtasarov.domain.request.WriterRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.WriterResponseTo;
import com.bsuir.romanmuhtasarov.exceptions.NoSuchWriterException;
import com.bsuir.romanmuhtasarov.repositories.WriterRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.bsuir.romanmuhtasarov.serivces.WriterService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Validated
public class WriterServiceImpl implements WriterService {
    private final WriterRepository writerRepository;
    private final WriterMapper writerMapper;
    private final WriterListMapper writerListMapper;

    @Autowired
    public WriterServiceImpl(WriterRepository writerRepository, WriterMapper writerMapper, WriterListMapper writerListMapper) {
        this.writerRepository = writerRepository;
        this.writerMapper = writerMapper;
        this.writerListMapper = writerListMapper;
    }

    @Override
    @Validated(ValidationMarker.OnCreate.class)
    public WriterResponseTo create(@Valid WriterRequestTo entity) {
        return writerMapper.toWriterResponseTo(writerRepository.save(writerMapper.toWriter(entity)));
    }

    @Override
    public List<WriterResponseTo> read() {
        return writerListMapper.toWriterResponseToList(writerRepository.findAll());
    }

    // Можно сразу сделать проверку != и выкинуть исключение, но так более читабельно :)
    @Override
    @Validated(ValidationMarker.OnUpdate.class)
    public WriterResponseTo update(@Valid WriterRequestTo entity) {
        if (writerRepository.existsById(entity.id())) {
            Writer writer = writerMapper.toWriter(entity);
            writer.setNewslist(writerRepository.getReferenceById(writer.getId()).getNewslist());
            return writerMapper.toWriterResponseTo(writerRepository.save(writer));
        } else {
            throw new NoSuchWriterException(entity.id());
        }

    }

    @Override
    public void delete(Long id) {
        if (writerRepository.existsById(id)) {
            writerRepository.deleteById(id);
        } else {
            throw new NoSuchWriterException(id);
        }
    }

    @Override
    public WriterResponseTo findWriterById(Long id) {
        Writer writer = writerRepository.findById(id).orElseThrow(() -> new NoSuchWriterException(id));
        return writerMapper.toWriterResponseTo(writer);
    }

    @Override
    public Optional<Writer> findWriterByIdExt(Long id) {
        return writerRepository.findById(id);
    }
}
