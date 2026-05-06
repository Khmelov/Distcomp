package com.github.Lexya06.startrestapp.publisher.impl.service.abstraction;

import com.github.Lexya06.startrestapp.publisher.impl.model.entity.abstraction.BaseEntity;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.impl.MyCrudRepositoryImpl;
import com.github.Lexya06.startrestapp.publisher.impl.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.impl.GenericMapperImpl;
import com.querydsl.core.types.Predicate;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

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
