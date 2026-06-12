package by.bsuir.distcomp.controller.v2;

import by.bsuir.distcomp.dto.TagDto;
import by.bsuir.distcomp.security.AuthorizationService;
import by.bsuir.distcomp.service.TagService;
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
@RequestMapping("/api/v2.0/tags")
public class TagV2Controller {
    private final TagService service;
    private final AuthorizationService authorization;

    public TagV2Controller(TagService service, AuthorizationService authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagDto create(@Valid @RequestBody TagDto dto, Authentication authentication) {
        authorization.requireAdmin(authentication);
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public TagDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<TagDto> findAll(@PageableDefault(size = 50, sort = "id") Pageable pageable) {
        return service.findAll(pageable);
    }

    @PutMapping("/{id}")
    public TagDto update(@PathVariable Long id, @Valid @RequestBody TagDto dto, Authentication authentication) {
        authorization.requireAdmin(authentication);
        return service.update(id, dto);
    }

    @PutMapping
    public TagDto update(@Valid @RequestBody TagDto dto, Authentication authentication) {
        authorization.requireAdmin(authentication);
        return service.update(dto.id(), dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        authorization.requireAdmin(authentication);
        service.delete(id);
    }
}
