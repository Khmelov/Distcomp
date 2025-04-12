package com.bsuir.romanmuhtasarov.serivces.impl;

import com.bsuir.romanmuhtasarov.domain.entity.Label;
import com.bsuir.romanmuhtasarov.domain.entity.ValidationMarker;
import com.bsuir.romanmuhtasarov.domain.mapper.LabelMapper;
import com.bsuir.romanmuhtasarov.domain.request.LabelRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.LabelResponseTo;
import com.bsuir.romanmuhtasarov.exceptions.NoSuchMessageException;
import com.bsuir.romanmuhtasarov.exceptions.NoSuchLabelException;
import com.bsuir.romanmuhtasarov.serivces.LabelService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.bsuir.romanmuhtasarov.domain.mapper.LabelListMapper;
import com.bsuir.romanmuhtasarov.repositories.LabelRepository;

import java.util.List;

@Service
@Transactional
@Validated
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;
    private final LabelListMapper labelListMapper;

    @Autowired
    public LabelServiceImpl(LabelRepository labelRepository, LabelMapper labelMapper, LabelListMapper labelListMapper) {
        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
        this.labelListMapper = labelListMapper;
    }

    @Override
    @Validated(ValidationMarker.OnCreate.class)
    public LabelResponseTo create(@Valid LabelRequestTo entity) {
        return labelMapper.toLabelResponseTo(labelRepository.save(labelMapper.toLabel(entity)));
    }

    @Override
    public List<LabelResponseTo> read() {
        return labelListMapper.toLabelResponseToList(labelRepository.findAll());
    }

    @Override
    @Validated(ValidationMarker.OnUpdate.class)
    public LabelResponseTo update(@Valid LabelRequestTo entity) {
        if (labelRepository.existsById(entity.id())) {
            Label label = labelMapper.toLabel(entity);
            Label labelRef = labelRepository.getReferenceById(label.getId());
            label.setNewsLabelList(labelRef.getNewsLabelList());
            return labelMapper.toLabelResponseTo(labelRepository.save(label));
        } else {
            throw new NoSuchMessageException(entity.id());
        }
    }

    @Override
    public void delete(Long id) {
        if (labelRepository.existsById(id)) {
            labelRepository.deleteById(id);
        } else {
            throw new NoSuchMessageException(id);
        }
    }

    @Override
    public LabelResponseTo findLabelById(Long id) {
        Label label = labelRepository.findById(id).orElseThrow(() -> new NoSuchLabelException(id));
        return labelMapper.toLabelResponseTo(label);
    }
}
