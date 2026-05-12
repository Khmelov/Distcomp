package by.shaminko.distcomp.controllers.v2;

import by.shaminko.distcomp.dto.ReactionRequestTo;
import by.shaminko.distcomp.dto.ReactionResponseTo;
import by.shaminko.distcomp.entities.UserRole;
import by.shaminko.distcomp.security.user.AuthenticatedUser;
import by.shaminko.distcomp.services.ReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping({"/api/v2.0/messages", "/api/v2.0/notices", "/api/v2.0/reactions"})
@RequiredArgsConstructor
public class MessageV2Controller {
    private final ReactionService reactionService;

    @GetMapping
    public Collection<ReactionResponseTo> getAll() {
        return reactionService.getAll();
    }

    @GetMapping("/{id}")
    public ReactionResponseTo getById(@PathVariable Long id) {
        return reactionService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReactionResponseTo create(@RequestBody @Valid ReactionRequestTo request, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (user.getRole() == UserRole.CUSTOMER) {
            request.setCreatorId(user.getId());
        }
        return reactionService.create(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.isReactionOwner(#id, authentication)")
    public ReactionResponseTo update(
            @PathVariable Long id,
            @RequestBody @Valid ReactionRequestTo request,
            Authentication authentication
    ) {
        request.setId(id);
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (user.getRole() == UserRole.CUSTOMER) {
            request.setCreatorId(user.getId());
        }
        return reactionService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.isReactionOwner(#id, authentication)")
    public void delete(@PathVariable Long id) {
        reactionService.delete(id);
    }
}
