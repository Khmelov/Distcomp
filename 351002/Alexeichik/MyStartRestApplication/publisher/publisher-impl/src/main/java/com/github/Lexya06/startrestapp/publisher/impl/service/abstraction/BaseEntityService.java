package com.github.Lexya06.startrestapp.publisher.impl.service.abstraction;

import com.github.Lexya06.startrestapp.publisher.impl.model.entity.abstraction.BaseEntity;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.impl.MyCrudRepositoryImpl;
import com.github.Lexya06.startrestapp.publisher.impl.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.impl.GenericMapperImpl;
import com.querydsl.core.types.Predicate;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class BaseEntityService<T extends BaseEntity, RequestDTO, ResponseDTO> {
    @Getter
    Class<T> entityClass;
    public BaseEntityService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    // abstractions to reduce code count
    protected abstract MyCrudRepositoryImpl<T> getRepository();
    protected abstract GenericMapperImpl<T,RequestDTO,ResponseDTO> getMapper();

    protected void validate(RequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
    }

    protected void preCreate(T entity, RequestDTO requestDTO) {}

    protected void preUpdate(T entity, RequestDTO requestDTO) {}

    @Transactional
    public ResponseDTO createEntity(RequestDTO requestDTO) {
        validate(requestDTO);
        T entity = getMapper().createEntityFromRequest(requestDTO);
        preCreate(entity, requestDTO);
        entity = getRepository().save(entity);
        return getMapper().createResponseFromEntity(entity);
    }

    @Transactional
    public ResponseDTO updateEntity(Long id, RequestDTO requestDTO) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null for update");
        }
        validate(requestDTO);
        T entity = getRepository().findById(id).orElseThrow(()->new MyEntityNotFoundException(id, entityClass));
        getMapper().updateEntityFromRequest(requestDTO, entity);
        preUpdate(entity, requestDTO);
        entity = getRepository().save(entity);
        return getMapper().createResponseFromEntity(entity);
    }

    @Transactional(readOnly = true)
    public List<ResponseDTO> getEntities(Predicate predicate, Pageable pageable) {
        List<T> entities = getRepository().findAll(predicate, pageable).getContent();
        return getMapper().createResponseFromEntities(entities);
    }

    @Transactional
    public void deleteEntityById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null for deletion");
        }
        if (!getRepository().existsById(id)) {
            throw new MyEntityNotFoundException(id, entityClass);
        }
        getRepository().deleteById(id);
    }

    @Transactional(readOnly = true)
    public ResponseDTO getEntityById(Long id) {
        T entity = getRepository().findById(id).orElseThrow(()->new MyEntityNotFoundException(id, entityClass));
        return getMapper().createResponseFromEntity(entity);
    }

    @Transactional(readOnly = true)
    public void validateExistence(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (!getRepository().existsById(id)) {
            throw new MyEntityNotFoundException(id, entityClass);
        }
    }


    public T getEntityReferenceWithCheckExistingId(Long id) {
        if (!getRepository().existsById(id)) {
            throw new MyEntityNotFoundException(id, entityClass);
        }
        return getRepository().getReferenceById(id);
    }

}
