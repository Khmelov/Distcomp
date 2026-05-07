package com.github.Lexya06.startrestapp.publisher.impl.service.realization;

import com.github.Lexya06.startrestapp.publisher.api.dto.label.LabelRequestTo;
import com.github.Lexya06.startrestapp.publisher.api.dto.label.LabelResponseTo;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.QLabel;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.impl.MyCrudRepositoryImpl;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.realization.LabelRepository;
import com.github.Lexya06.startrestapp.publisher.impl.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.impl.GenericMapperImpl;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.realization.LabelMapper;
import com.querydsl.core.types.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "labels")
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

    @Override
    @Cacheable(key = "#id")
    public LabelResponseTo getEntityById(Long id) {
        return super.getEntityById(id);
    }

    @Override
    @CachePut(key = "#result.id")
    public LabelResponseTo createEntity(LabelRequestTo requestDTO) {
        return super.createEntity(requestDTO);
    }

    @Override
    @CachePut(key = "#id")
    public LabelResponseTo updateEntity(Long id, LabelRequestTo requestDTO) {
        return super.updateEntity(id, requestDTO);
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteEntityById(Long id) {
        super.deleteEntityById(id);
    }
}
