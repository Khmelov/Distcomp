package by.shaminko.distcomp.controllers.v2;

import by.shaminko.distcomp.dto.TagRequestTo;
import by.shaminko.distcomp.dto.TagResponseTo;
import by.shaminko.distcomp.services.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping({"/api/v2.0/markers", "/api/v2.0/labels"})
@RequiredArgsConstructor
public class MarkerV2Controller {
    private final TagService tagService;

    @GetMapping
    public Collection<TagResponseTo> getAll() {
        return tagService.getAll();
    }

    @GetMapping("/{id}")
    public TagResponseTo getById(@PathVariable Long id) {
        return tagService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TagResponseTo create(@RequestBody @Valid TagRequestTo request) {
        return tagService.create(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public TagResponseTo update(@RequestBody @Valid TagRequestTo request) {
        return tagService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        tagService.delete(id);
    }
}
