package com.github.Lexya06.startrestapp.publisher.impl.service.mapper.impl;


import com.github.Lexya06.startrestapp.publisher.impl.model.entity.abstraction.BaseEntity;
import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface GenericMapperImpl<T extends BaseEntity, RequestDTO, ResponseDTO> {
    // mapping entity into response
    ResponseDTO createResponseFromEntity(T entity);

    // mapping request into new entity
    T createEntityFromRequest(RequestDTO dto);

    Set<T> createEntitiesFromRequest(List<RequestDTO> dtos);

    // mapping request into existing entity
    void updateEntityFromRequest(RequestDTO dto, @MappingTarget T entity);

    // mapping all entities into response set
    List<ResponseDTO> createResponseFromEntities(Collection<T> entities);
}
