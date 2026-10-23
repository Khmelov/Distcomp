package com.distcomp.mapper;

import com.distcomp.dto.request.CreatorRequestTo;
import com.distcomp.dto.response.CreatorResponseTo;
import com.distcomp.model.Creator;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;
import java.util.List;

/**
 * Converts {@link Creator} entities to and from their transport objects.
 */
@Mapper(componentModel = "spring")
public interface CreatorMapper {

    @Mapping(target = "id", ignore = true)
    Creator toEntity(CreatorRequestTo request);

    CreatorResponseTo toResponse(Creator creator);

    List<CreatorResponseTo> toResponseList(Collection<Creator> creators);

    /** Copies only the fields that are present in the request. */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(CreatorRequestTo request, @MappingTarget Creator creator);
}
