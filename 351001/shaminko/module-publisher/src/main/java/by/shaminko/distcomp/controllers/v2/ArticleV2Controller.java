package by.shaminko.distcomp.controllers.v2;

import by.shaminko.distcomp.dto.TweetRequestTo;
import by.shaminko.distcomp.dto.TweetResponseTo;
import by.shaminko.distcomp.entities.UserRole;
import by.shaminko.distcomp.security.user.AuthenticatedUser;
import by.shaminko.distcomp.services.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/v2.0/articles")
@RequiredArgsConstructor
public class ArticleV2Controller {
    private final TweetService tweetService;

    @GetMapping
    public Collection<TweetResponseTo> getAll() {
        return tweetService.getAll();
    }

    @GetMapping("/{id}")
    public TweetResponseTo getById(@PathVariable Long id) {
        return tweetService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseTo create(@RequestBody @Valid TweetRequestTo request, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (user.getRole() == UserRole.CUSTOMER) {
            request.setCreatorId(user.getId());
        }
        return tweetService.create(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.isTweetOwner(#request.id, authentication)")
    public TweetResponseTo update(@RequestBody @Valid TweetRequestTo request, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (user.getRole() == UserRole.CUSTOMER) {
            request.setCreatorId(user.getId());
        }
        return tweetService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.isTweetOwner(#id, authentication)")
    public void delete(@PathVariable Long id) {
        tweetService.delete(id);
    }
}

