package org.example;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring", uses = TagMapper.class)
public interface StoryMapper {

    // ---------- REQUEST → ENTITY ----------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "writer", ignore = true)
    @Mapping(target = "storyTags", ignore = true)
    @Mapping(target = "notices", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Story toEntity(StoryRequestTo dto);

    // ---------- ENTITY → RESPONSE ----------
    @Mapping(target = "writerId", source = "writer.id")
    @Mapping(target = "created", source = "createdAt")
    @Mapping(target = "modified", source = "updatedAt")
    @Mapping(target = "tags", expression = "java(mapTags(entity))")
    StoryResponseTo toResponse(Story entity);

    default List<TagShortResponseTo> mapTags(Story story) {
        return story.getStoryTags()
                .stream()
                .map(st -> new TagShortResponseTo(
                        st.getTag().getId(),
                        st.getTag().getName()
                ))
                .toList();
    }
    default OffsetDateTime map(LocalDateTime time) {
        return time == null ? null : time.atOffset(ZoneOffset.UTC);
    }


}