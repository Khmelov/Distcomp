package by.bsuir.discussion.service;

import by.bsuir.discussion.dto.MessageRequestTo;
import by.bsuir.discussion.dto.MessageResponseTo;
import by.bsuir.discussion.dto.MessageState;
import by.bsuir.discussion.entity.MessageById;
import by.bsuir.discussion.entity.MessageByStory;
import by.bsuir.discussion.entity.MessageKey;
import by.bsuir.discussion.exception.ApiException;
import by.bsuir.discussion.exception.ErrorCode;
import by.bsuir.discussion.repository.MessageByIdRepository;
import by.bsuir.discussion.repository.MessageByStoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MessageService {

    private final MessageByStoryRepository byStory;
    private final MessageByIdRepository byId;
    private final MessageIds ids;
    private final RequestValidator requestValidator;
    private final Moderator moderator;

    public MessageService(MessageByStoryRepository byStory,
                          MessageByIdRepository byId,
                          MessageIds ids,
                          RequestValidator requestValidator,
                          Moderator moderator) {
        this.byStory = byStory;
        this.byId = byId;
        this.ids = ids;
        this.requestValidator = requestValidator;
        this.moderator = moderator;
    }

    public MessageResponseTo create(MessageRequestTo request) {
        requestValidator.validate(request);
        long id = request.getId() != null ? request.getId() : ids.nextId();
        return store(id, request.getStoryId(), request.getContent());
    }

    public MessageResponseTo getById(Long id) {
        MessageById row = byId.findById(id).orElseThrow(() -> ApiException.notFound(id));
        return response(row.getId(), row.getStoryId(), row.getContent(), row.getState());
    }

    public List<MessageResponseTo> getAll() {
        List<MessageResponseTo> result = new ArrayList<>();
        byId.findAll().forEach(row -> result.add(response(row.getId(), row.getStoryId(), row.getContent(), row.getState())));
        result.sort(Comparator.comparing(MessageResponseTo::getId));
        return result;
    }

    public List<MessageResponseTo> findByStoryId(Long storyId) {
        return byStory.findByStoryId(storyId).stream()
                .map(row -> response(row.getKey().getId(), row.getKey().getStoryId(), row.getContent(), row.getState()))
                .sorted(Comparator.comparing(MessageResponseTo::getId))
                .toList();
    }

    public MessageResponseTo update(Long pathId, MessageRequestTo request) {
        requestValidator.validate(request);
        Long id = resolveId(pathId, request.getId());
        MessageById existing = byId.findById(id).orElseThrow(() -> ApiException.notFound(id));
        if (!existing.getStoryId().equals(request.getStoryId())) {
            byStory.deleteById(new MessageKey(existing.getStoryId(), id));
        }
        return store(id, request.getStoryId(), request.getContent());
    }

    public void delete(Long id) {
        MessageById existing = byId.findById(id).orElseThrow(() -> ApiException.notFound(id));
        byStory.deleteById(new MessageKey(existing.getStoryId(), id));
        byId.deleteById(id);
    }

    private MessageResponseTo store(Long id, Long storyId, String content) {
        MessageState state = moderator.decide(content);
        save(id, storyId, content, state);
        return response(id, storyId, content, state.name());
    }

    private void save(Long id, Long storyId, String content, MessageState state) {
        MessageByStory storyRow = new MessageByStory();
        storyRow.setKey(new MessageKey(storyId, id));
        storyRow.setContent(content);
        storyRow.setState(state.name());
        byStory.save(storyRow);

        MessageById idRow = new MessageById();
        idRow.setId(id);
        idRow.setStoryId(storyId);
        idRow.setContent(content);
        idRow.setState(state.name());
        byId.save(idRow);
    }

    private Long resolveId(Long pathId, Long bodyId) {
        if (pathId != null && bodyId != null && !pathId.equals(bodyId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.ID_MISMATCH,
                    "Path id and body id do not match");
        }
        Long id = pathId != null ? pathId : bodyId;
        if (id == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, ErrorCode.ID_REQUIRED, "id is required");
        }
        return id;
    }

    private MessageResponseTo response(Long id, Long storyId, String content, String state) {
        MessageResponseTo response = new MessageResponseTo(id, storyId, content);
        if (state != null) {
            response.setState(MessageState.valueOf(state));
        }
        return response;
    }
}
