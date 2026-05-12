package by.bsuir.task310.mapper;

import by.bsuir.task310.dto.request.StoryRequestTo;
import by.bsuir.task310.dto.response.StoryResponseTo;
import by.bsuir.task310.entity.Story;

public final class StoryMapper {
    private StoryMapper() {
    }

    public static Story toEntity(StoryRequestTo request) {
        return new Story(
                request.id(),
                request.editorId(),
                request.title(),
                request.content(),
                null,
                null,
                request.tagIds()
        );
    }

    public static StoryResponseTo toResponse(Story story) {
        return new StoryResponseTo(
                story.getId(),
                story.getEditorId(),
                story.getTitle(),
                story.getContent(),
                story.getCreated(),
                story.getModified(),
                story.getTagIds()
        );
    }
}
