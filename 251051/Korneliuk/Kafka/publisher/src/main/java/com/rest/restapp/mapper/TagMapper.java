package com.rest.restapp.mapper;

import com.rest.restapp.dto.request.TagRequestTo;
import com.rest.restapp.dto.response.TagResponseTo;
import com.rest.restapp.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    Tag toEntity(TagRequestTo requestTo);

    TagResponseTo toResponseTo(Tag entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(TagRequestTo requestTo, @MappingTarget Tag entity);
}