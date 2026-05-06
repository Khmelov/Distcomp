package by.boukhvalova.distcomp.controllers.v2;

import by.boukhvalova.distcomp.dto.NoteRequestTo;
import by.boukhvalova.distcomp.dto.NoteResponseTo;
import by.boukhvalova.distcomp.entities.UserRole;
import by.boukhvalova.distcomp.security.user.AuthenticatedUser;
import by.boukhvalova.distcomp.services.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping({"/api/v2.0/notes", "/api/v2.0/messages", "/api/v2.0/notices", "/api/v2.0/reactions"})
@RequiredArgsConstructor
public class NoteV2Controller {
    private final NoteService noteService;

    @GetMapping
    public Collection<NoteResponseTo> getAll() {
        return noteService.getAll();
    }

    @GetMapping("/{id}")
    public NoteResponseTo getById(@PathVariable Long id) {
        return noteService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponseTo create(@RequestBody @Valid NoteRequestTo request, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (user.getRole() == UserRole.CUSTOMER) {
            request.setUserId(user.getId());
        }
        return noteService.create(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.isNoteOwner(#id, authentication)")
    public NoteResponseTo update(
            @PathVariable Long id,
            @RequestBody @Valid NoteRequestTo request,
            Authentication authentication
    ) {
        request.setId(id);
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (user.getRole() == UserRole.CUSTOMER) {
            request.setUserId(user.getId());
        }
        return noteService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.isNoteOwner(#id, authentication)")
    public void delete(@PathVariable Long id) {
        noteService.delete(id);
    }
}
