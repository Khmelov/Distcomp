package by.bsuir.task310.mapper;

import by.bsuir.task310.dto.request.TagRequestTo;
import by.bsuir.task310.dto.response.TagResponseTo;
import by.bsuir.task310.entity.Tag;

public final class TagMapper {
    private TagMapper() {
    }

    public static Tag toEntity(TagRequestTo request) {
        return new Tag(request.id(), request.name());
    }

    public static TagResponseTo toResponse(Tag tag) {
        return new TagResponseTo(tag.getId(), tag.getName());
    }
}
