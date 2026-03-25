package com.github.Lexya06.startrestapp.service.abstraction;

import com.github.Lexya06.startrestapp.model.entity.abstraction.BaseEntity;
import com.github.Lexya06.startrestapp.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.model.repository.impl.MyCrudRepositoryImpl;
import com.github.Lexya06.startrestapp.service.customexception.MyEntitiesNotFoundException;
import com.github.Lexya06.startrestapp.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.service.mapper.impl.GenericMapperImpl;
import com.querydsl.core.types.Predicate;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseEntityService<T extends BaseEntity, RequestDTO, ResponseDTO> {
    @Getter
    Class<T> entityClass;
    public BaseEntityService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    // abstractions to reduce code count
    protected abstract MyCrudRepositoryImpl<T> getRepository();
    protected abstract GenericMapperImpl<T,RequestDTO,ResponseDTO> getMapper();
    public ResponseDTO createEntity(RequestDTO requestDTO) {
        T entity = getMapper().createEntityFromRequest(requestDTO);
        entity = getRepository().save(entity);
        return getMapper().createResponseFromEntity(entity);
    }

    public ResponseDTO updateEntity(Long id, RequestDTO requestDTO) {
        T entity = getRepository().findById(id).orElseThrow(()->new MyEntityNotFoundException(id, entityClass));
        getMapper().updateEntityFromRequest(requestDTO, entity);
        entity = getRepository().save(entity);
        return getMapper().createResponseFromEntity(entity);
    }

    public List<ResponseDTO> getEntities(Predicate predicate, Pageable pageable) {
        List<T> entities = getRepository().findAll(predicate, pageable).getContent();
        return getMapper().createResponseFromEntities(entities);
    }

    public void deleteEntityById(Long id) {
        if (!getRepository().existsById(id)) {
            throw new MyEntityNotFoundException(id, entityClass);
        }
        getRepository().deleteById(id);
    }

    public ResponseDTO getEntityById(Long id) {
        T entity = getRepository().findById(id).orElseThrow(()->new MyEntityNotFoundException(id, entityClass));
        return getMapper().createResponseFromEntity(entity);
    }


    public T getEntityReferenceWithCheckExistingId(Long id) {
        if (!getRepository().existsById(id)) {
            throw new MyEntityNotFoundException(id, entityClass);
        }
        return getRepository().getReferenceById(id);
    }

}
