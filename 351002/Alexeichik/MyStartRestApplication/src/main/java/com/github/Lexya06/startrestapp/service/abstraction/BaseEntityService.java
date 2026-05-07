package com.github.Lexya06.startrestapp.service.abstraction;

import com.github.Lexya06.startrestapp.model.entity.abstraction.BaseEntity;
import com.github.Lexya06.startrestapp.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.service.mapper.impl.GenericMapperImpl;
import com.github.Lexya06.startrestapp.model.repository.abstraction.MyCrudRepository;

import java.util.Set;

public abstract class BaseEntityService<T extends BaseEntity, RequestDTO, ResponseDTO> {
    Class<T> entityClass;
    public BaseEntityService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    // abstractions to reduce code count
    protected abstract MyCrudRepository<T> getRepository();
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

    public Set<ResponseDTO> getEntities() {
        Set<T> entities = getRepository().findAll();
        return getMapper().createResponseFromEntities(entities);
    }

    public void deleteEntityById(Long id) {
        getRepository().findById(id).orElseThrow(()->new MyEntityNotFoundException(id, entityClass));
        getRepository().deleteById(id);
    }

    public ResponseDTO getEntityById(Long id) {
        T entity = getRepository().findById(id).orElseThrow(()->new MyEntityNotFoundException(id, entityClass));
        return getMapper().createResponseFromEntity(entity);
    }
}
