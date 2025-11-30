package com.rest.restapp.mapper;

import com.rest.restapp.dto.request.TagRequestToDto;
import com.rest.restapp.dto.response.TagResponseToDto;
import com.rest.restapp.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    Tag toEntity(TagRequestToDto requestTo);

    TagResponseToDto toResponseTo(Tag entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(TagRequestToDto requestTo, @MappingTarget Tag entity);
}