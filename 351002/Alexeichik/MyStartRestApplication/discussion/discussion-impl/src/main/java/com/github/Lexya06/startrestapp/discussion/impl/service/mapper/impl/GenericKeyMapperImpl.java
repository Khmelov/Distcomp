package com.github.Lexya06.startrestapp.discussion.impl.service.mapper.impl;

import org.mapstruct.MappingTarget;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface GenericKeyMapperImpl<K, KDto> {
    KDto createDtoFromKey(K key);

    // mapping request into new entity
    K createKeyFromDto(KDto dto);

    Set<K> createKeysFromDto(List<KDto> dtos);

    // mapping request into existing entity
    void updateKeyFromDto(KDto dto, @MappingTarget K key);

    // mapping all entities into response set
    List<K> createDtoFromKeys(Collection<K> keys);
}
