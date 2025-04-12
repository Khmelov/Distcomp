package com.bsuir.romanmuhtasarov.serivces.impl;

import com.bsuir.romanmuhtasarov.domain.entity.Creator;
import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;
import com.bsuir.romanmuhtasarov.domain.mapper.CreatorListMapper;
import com.bsuir.romanmuhtasarov.domain.mapper.CreatorMapper;
import com.bsuir.romanmuhtasarov.domain.request.CreatorRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.CreatorResponseTo;
import com.bsuir.romanmuhtasarov.exceptions.NoSuchCreatorException;
import com.bsuir.romanmuhtasarov.repositories.CreatorRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.bsuir.romanmuhtasarov.serivces.CreatorService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Validated
public class CreatorServiceImpl implements CreatorService {
    private final CreatorRepository creatorRepository;
    private final CreatorMapper creatorMapper;
    private final CreatorListMapper creatorListMapper;

    @Autowired
    public CreatorServiceImpl(CreatorRepository creatorRepository, CreatorMapper creatorMapper, CreatorListMapper creatorListMapper) {
        this.creatorRepository = creatorRepository;
        this.creatorMapper = creatorMapper;
        this.creatorListMapper = creatorListMapper;
    }

    @Override
    @Validated(ValidationMarker.OnCreate.class)
    public CreatorResponseTo create(@Valid CreatorRequestTo entity) {
        return creatorMapper.toCreatorResponseTo(creatorRepository.save(creatorMapper.toCreator(entity)));
    }

    @Override
    public List<CreatorResponseTo> read() {
        return creatorListMapper.toCreatorResponseToList(creatorRepository.findAll());
    }

    // Можно сразу сделать проверку != и выкинуть исключение, но так более читабельно :)
    @Override
    @Validated(ValidationMarker.OnUpdate.class)
    public CreatorResponseTo update(@Valid CreatorRequestTo entity) {
        if (creatorRepository.existsById(entity.id())) {
            Creator creator = creatorMapper.toCreator(entity);
            creator.setNewslist(creatorRepository.getReferenceById(creator.getId()).getNewslist());
            return creatorMapper.toCreatorResponseTo(creatorRepository.save(creator));
        } else {
            throw new NoSuchCreatorException(entity.id());
        }

    }

    @Override
    public void delete(Long id) {
        if (creatorRepository.existsById(id)) {
            creatorRepository.deleteById(id);
        } else {
            throw new NoSuchCreatorException(id);
        }
    }

    @Override
    public CreatorResponseTo findCreatorById(Long id) {
        Creator creator = creatorRepository.findById(id).orElseThrow(() -> new NoSuchCreatorException(id));
        return creatorMapper.toCreatorResponseTo(creator);
    }

    @Override
    public Optional<Creator> findCreatorByIdExt(Long id) {
        return creatorRepository.findById(id);
    }
}
