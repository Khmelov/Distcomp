package by.bsuir.rest.service;

import by.bsuir.rest.dto.request.StoryRequestTo;
import by.bsuir.rest.dto.response.MarkerResponseTo;
import by.bsuir.rest.dto.response.StoryResponseTo;
import by.bsuir.rest.dto.response.UserResponseTo;

import java.util.List;

public interface StoryService {

    StoryResponseTo create(StoryRequestTo request);

    StoryResponseTo getById(Long id);

    List<StoryResponseTo> find(StorySearchCriteria criteria);

    StoryResponseTo update(Long pathId, StoryRequestTo request);

    void delete(Long id);

    UserResponseTo getAuthor(Long storyId);

    List<MarkerResponseTo> getMarkers(Long storyId);
}
