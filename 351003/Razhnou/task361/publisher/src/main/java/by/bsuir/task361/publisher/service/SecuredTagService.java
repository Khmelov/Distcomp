package by.bsuir.task361.publisher.service;

import by.bsuir.task361.publisher.dto.request.TagRequestTo;
import by.bsuir.task361.publisher.dto.response.TagResponseTo;
import by.bsuir.task361.publisher.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecuredTagService {
    private final TagService tagService;
    private final CurrentUserService currentUserService;

    public SecuredTagService(TagService tagService, CurrentUserService currentUserService) {
        this.tagService = tagService;
        this.currentUserService = currentUserService;
    }

    public TagResponseTo create(TagRequestTo request) {
        requireAdmin();
        return tagService.create(request);
    }

    public List<TagResponseTo> findAll() {
        return tagService.findAll();
    }

    public TagResponseTo findById(Long id) {
        return tagService.findById(id);
    }

    public TagResponseTo update(TagRequestTo request) {
        requireAdmin();
        return tagService.update(request);
    }

    public void delete(Long id) {
        requireAdmin();
        tagService.delete(id);
    }

    private void requireAdmin() {
        if (!currentUserService.isAdmin()) {
            throw new ApiException(HttpStatus.FORBIDDEN, 40301, "Access denied");
        }
    }
}
