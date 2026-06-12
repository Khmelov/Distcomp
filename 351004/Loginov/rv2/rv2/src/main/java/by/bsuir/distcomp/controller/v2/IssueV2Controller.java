package by.bsuir.distcomp.controller.v2;

import by.bsuir.distcomp.dto.IssueDto;
import by.bsuir.distcomp.security.AuthorizationService;
import by.bsuir.distcomp.service.IssueService;
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
@RequestMapping("/api/v2.0/issues")
public class IssueV2Controller {
    private final IssueService service;
    private final AuthorizationService authorization;

    public IssueV2Controller(IssueService service, AuthorizationService authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IssueDto create(@Valid @RequestBody IssueDto dto, Authentication authentication) {
        if (!authorization.isAdmin(authentication)) {
            authorization.requireSelfOrAdmin(authentication, dto.writerId());
        }
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public IssueDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<IssueDto> findAll(@PageableDefault(size = 50, sort = "id") Pageable pageable) {
        return service.findAll(pageable);
    }

    @PutMapping("/{id}")
    public IssueDto update(@PathVariable Long id, @Valid @RequestBody IssueDto dto, Authentication authentication) {
        authorization.requireIssueOwnerOrAdmin(authentication, id);
        return service.update(id, dto);
    }

    @PutMapping
    public IssueDto update(@Valid @RequestBody IssueDto dto, Authentication authentication) {
        authorization.requireIssueOwnerOrAdmin(authentication, dto.id());
        return service.update(dto.id(), dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        authorization.requireIssueOwnerOrAdmin(authentication, id);
        service.delete(id);
    }
}
