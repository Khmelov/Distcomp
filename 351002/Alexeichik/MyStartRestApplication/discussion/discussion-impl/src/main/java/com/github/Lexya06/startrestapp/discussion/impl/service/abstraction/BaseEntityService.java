package com.github.Lexya06.startrestapp.discussion.impl.service.abstraction;

import com.datastax.oss.protocol.internal.util.Bytes;
import com.github.Lexya06.startrestapp.discussion.api.searchcriteria.abstraction.BaseSearchCriteria;
import com.github.Lexya06.startrestapp.discussion.impl.model.entity.abstraction.BaseEntity;
import com.github.Lexya06.startrestapp.discussion.impl.model.repository.impl.MyCrudRepositoryImpl;
import com.github.Lexya06.startrestapp.discussion.impl.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.discussion.api.dto.PagedResponse;
import com.github.Lexya06.startrestapp.discussion.impl.service.mapper.impl.GenericKeyMapperImpl;
import com.github.Lexya06.startrestapp.discussion.impl.service.mapper.impl.GenericMapperImpl;
import lombok.Getter;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseEntityService<T extends BaseEntity<DBKey>, DBKey, APIKey, RequestDTO, ResponseDTO, C extends BaseSearchCriteria> {
    @Getter
    Class<T> entityClass;
    public BaseEntityService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    // abstractions to reduce code count
    protected abstract MyCrudRepositoryImpl<T, DBKey> getRepository();
    protected abstract GenericMapperImpl<T, DBKey,RequestDTO,ResponseDTO> getMapper();
    protected abstract GenericKeyMapperImpl<DBKey, APIKey> getKeyMapper();

    protected void validate(RequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }
    }

    protected void preCreate(T entity, RequestDTO requestDTO) {}

    protected void preUpdate(T entity, RequestDTO requestDTO) {}

    public ResponseDTO createEntity(RequestDTO requestDTO) {
        validate(requestDTO);
        T entity = getMapper().createEntityFromRequest(requestDTO);
        preCreate(entity, requestDTO);
        entity = getRepository().save(entity);
        return getMapper().createResponseFromEntity(entity);
    }

    public ResponseDTO updateEntity(APIKey apiKey, RequestDTO requestDTO) {
        if (apiKey == null) {
            throw new IllegalArgumentException("API key cannot be null for update");
        }
        validate(requestDTO);
        DBKey dbKey = getKeyMapper().createKeyFromDto(apiKey);
        T entity = getRepository().findById(dbKey).orElseThrow(()->new MyEntityNotFoundException(apiKey.toString(), entityClass));
        getMapper().updateEntityFromRequest(requestDTO, entity);
        preUpdate(entity, requestDTO);
        entity = getRepository().save(entity);
        return getMapper().createResponseFromEntity(entity);
    }

    public PagedResponse<ResponseDTO> getAllEntitiesByCriteria(C criteria){
        List<ResponseDTO> dtoList;
        if (criteria.getSize() == null || criteria.getSize() == 0) {
            dtoList = getRepository().findAll().stream().map(getMapper()::createResponseFromEntity)
                    .toList();
            return PagedResponse.<ResponseDTO>builder()
                    .data(dtoList)
                    .nextPagingState(null)
                    .build();
        }

        Slice<T> slice = getEntitySlice(criteria);

        dtoList = slice.getContent().stream()
                .map(getMapper()::createResponseFromEntity)
                .toList();

        return PagedResponse.<ResponseDTO>builder()
                .data(dtoList)
                .nextPagingState(extractPagingState(slice))
                .build();

    }

    protected abstract Slice<T> getEntitySlice(C criteria);

    public void deleteEntityById(APIKey apiKey) {
        if (apiKey == null) {
            throw new IllegalArgumentException("API key cannot be null for deletion");
        }
        DBKey dbKey = getKeyMapper().createKeyFromDto(apiKey);
        if (!getRepository().existsById(dbKey)) {
            throw new MyEntityNotFoundException(apiKey.toString(), entityClass);
        }
        getRepository().deleteById(dbKey);
    }

    public ResponseDTO getEntityById(APIKey apiKey) {
        DBKey dbKey = getKeyMapper().createKeyFromDto(apiKey);
        T entity = getRepository().findById(dbKey).orElseThrow(()->new MyEntityNotFoundException(apiKey.toString(), entityClass));
        return getMapper().createResponseFromEntity(entity);
    }


    /**
     * Преобразует строковый токен и размер страницы в объект Pageable, понятный Кассандре.
     */
    protected Pageable createPageable(String pagingState, Integer size) {
        int pageSize = (size == null || size <= 0) ? 10 : size;
        if (pagingState != null && !pagingState.isEmpty()) {
            return CassandraPageRequest.of(
                    PageRequest.of(0, pageSize),
                    Bytes.fromHexString(pagingState)
            );
        }
        return PageRequest.of(0, pageSize);
    }

    /**
     * Безопасно извлекает Hex-строку следующего состояния пагинации из Slice.
     */
    protected String extractPagingState(Slice<?> slice) {

        if (slice.hasNext()) {
            CassandraPageRequest nextRequest = (CassandraPageRequest) slice.nextPageable();
            if (nextRequest.getPagingState() != null) {
                return Bytes.toHexString(nextRequest.getPagingState());
            }
        }
        return null;
    }

}
