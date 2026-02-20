package com.github.Lexya06.startrestapp.service.realization;

import com.github.Lexya06.startrestapp.model.dto.request.LabelRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.LabelResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.model.repository.abstraction.MyCrudRepository;
import com.github.Lexya06.startrestapp.model.repository.realization.LabelRepository;
import com.github.Lexya06.startrestapp.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.service.mapper.impl.GenericMapperImpl;
import com.github.Lexya06.startrestapp.service.mapper.realization.LabelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabelService extends BaseEntityService<Label, LabelRequestTo, LabelResponseTo> {

    LabelRepository labelRepository;
    LabelMapper labelMapper;

    @Autowired
    public LabelService(LabelRepository labelRepository, LabelMapper labelMapper) {
        super(Label.class);
        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
    }

    @Override
    protected MyCrudRepository<Label> getRepository() {
        return labelRepository;
    }

    @Override
    protected GenericMapperImpl<Label, LabelRequestTo, LabelResponseTo> getMapper() {
        return labelMapper;
    }
}
