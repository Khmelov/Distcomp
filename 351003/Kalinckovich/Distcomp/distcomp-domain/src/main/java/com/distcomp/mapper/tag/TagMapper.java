package com.distcomp.mapper.tag;

import com.distcomp.dto.tag.TagCreateRequest;
import com.distcomp.dto.tag.TagPatchRequest;
import com.distcomp.dto.tag.TagResponseDto;
import com.distcomp.dto.tag.TagUpdateRequest;
import com.distcomp.mapper.config.MappedConfig;
import com.distcomp.model.tag.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappedConfig.class)
public interface TagMapper {

    TagResponseDto toResponse(Tag entity);

    @Mapping(target = "id", ignore = true)
    Tag toEntity(TagCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    Tag updateFromDto(TagUpdateRequest dto, @MappingTarget Tag entity);

    @Mapping(target = "id", ignore = true)
    Tag updateFromPatch(TagPatchRequest dto, @MappingTarget Tag entity);
}
