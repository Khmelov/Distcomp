package by.bsuir.task330.publisher.mapper;

import by.bsuir.task330.publisher.dto.request.TagRequestTo;
import by.bsuir.task330.publisher.dto.response.TagResponseTo;
import by.bsuir.task330.publisher.entity.Tag;

public final class TagMapper {
    private TagMapper() {
    }

    public static Tag toEntity(TagRequestTo request) {
        Tag tag = new Tag();
        updateEntity(tag, request);
        return tag;
    }

    public static void updateEntity(Tag tag, TagRequestTo request) {
        tag.setName(request.name().trim());
    }

    public static TagResponseTo toResponse(Tag tag) {
        return new TagResponseTo(tag.getId(), tag.getName());
    }
}
