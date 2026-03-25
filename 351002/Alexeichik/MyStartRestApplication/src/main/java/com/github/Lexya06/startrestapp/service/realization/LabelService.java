package com.github.Lexya06.startrestapp.service.realization;

import com.github.Lexya06.startrestapp.model.dto.request.LabelRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.LabelResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.Article;
import com.github.Lexya06.startrestapp.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.model.entity.realization.QArticle;
import com.github.Lexya06.startrestapp.model.entity.realization.QLabel;
import com.github.Lexya06.startrestapp.model.repository.impl.MyCrudRepositoryImpl;
import com.github.Lexya06.startrestapp.model.repository.realization.ArticleRepository;
import com.github.Lexya06.startrestapp.model.repository.realization.LabelRepository;
import com.github.Lexya06.startrestapp.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.service.customexception.MyEntitiesNotFoundException;
import com.github.Lexya06.startrestapp.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.service.mapper.impl.GenericMapperImpl;
import com.github.Lexya06.startrestapp.service.mapper.realization.LabelMapper;
import com.querydsl.core.types.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LabelService extends BaseEntityService<Label, LabelRequestTo, LabelResponseTo> {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Autowired
    public LabelService(LabelRepository labelRepository, LabelMapper labelMapper) {
        super(Label.class);
        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
    }

    @Override
    protected MyCrudRepositoryImpl<Label> getRepository() {
        return labelRepository;
    }

    @Override
    protected GenericMapperImpl<Label, LabelRequestTo, LabelResponseTo> getMapper() {
        return labelMapper;
    }

    @Transactional
    public Set<Label> saveUnexistingLabelsByName(List<LabelRequestTo> requestList) {
        Set<Label> labelSet;
        Set<Label> allLabels;

        allLabels = labelMapper.createEntitiesFromRequest(requestList);

        QLabel qLabel = QLabel.label;
        Predicate predicate = qLabel.name.in(requestList.stream().map(LabelRequestTo::getName).collect(Collectors.toList()));
        Set<Label> existingLabels = new HashSet<>();
        labelRepository.findAll(predicate).forEach(existingLabels::add);
        Set<Label> newLabels = allLabels.stream().filter(nl -> !existingLabels.contains(nl)).collect(Collectors.toSet());
        if (newLabels.isEmpty()) {
            labelSet = existingLabels;

        }
        else {
            labelSet = new HashSet<>(labelRepository.saveAll(newLabels));
        }
        return labelSet;
    }


}
