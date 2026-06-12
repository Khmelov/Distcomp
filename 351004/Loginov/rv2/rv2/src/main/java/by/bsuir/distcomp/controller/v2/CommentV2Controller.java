package by.bsuir.distcomp.controller.v2;

import by.bsuir.distcomp.dto.CommentDto;
import by.bsuir.distcomp.security.AuthorizationService;
import by.bsuir.distcomp.service.CommentProxyService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2.0/comments")
public class CommentV2Controller {
    private final CommentProxyService service;
    private final AuthorizationService authorization;

    public CommentV2Controller(CommentProxyService service, AuthorizationService authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@Valid @RequestBody CommentDto dto, Authentication authentication) {
        authorization.requireIssueOwnerOrAdmin(authentication, dto.issueId());
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public CommentDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<CommentDto> findAll(@PageableDefault(size = 50, sort = "id") Pageable pageable) {
        return service.findAll(pageable);
    }

    @PutMapping("/{id}")
    public CommentDto update(@PathVariable Long id, @Valid @RequestBody CommentDto dto, Authentication authentication) {
        authorization.requireIssueOwnerOrAdmin(authentication, dto.issueId());
        return service.update(id, dto);
    }

    @PutMapping
    public CommentDto update(@Valid @RequestBody CommentDto dto, Authentication authentication) {
        authorization.requireIssueOwnerOrAdmin(authentication, dto.issueId());
        return service.update(dto.id(), dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        if (!authorization.isAdmin(authentication)) {
            authorization.requireIssueOwnerOrAdmin(authentication, service.get(id).issueId());
        }
        service.delete(id);
    }
}
