package by.shaminko.distcomp.controllers.v2;

import by.shaminko.distcomp.dto.EditorRequestTo;
import by.shaminko.distcomp.dto.EditorResponseTo;
import by.shaminko.distcomp.services.EditorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping({"/api/v2.0/creators", "/api/v2.0/users"})
@RequiredArgsConstructor
public class CreatorV2Controller {
    private final EditorService editorService;

    @GetMapping
    public Collection<EditorResponseTo> getAll() {
        return editorService.getAll();
    }

    @GetMapping("/{id}")
    public EditorResponseTo getById(@PathVariable Long id) {
        return editorService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EditorResponseTo register(@RequestBody @Valid EditorRequestTo request) {
        return editorService.create(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.isSelf(#request.id, authentication)")
    public EditorResponseTo update(@RequestBody @Valid EditorRequestTo request) {
        return editorService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.isSelf(#id, authentication)")
    public void delete(@PathVariable Long id) {
        editorService.delete(id);
    }
}
