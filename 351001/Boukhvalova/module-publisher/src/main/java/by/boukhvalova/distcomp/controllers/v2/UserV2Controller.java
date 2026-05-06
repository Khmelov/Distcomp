package by.boukhvalova.distcomp.controllers.v2;

import by.shaminko.distcomp.dto.UserRequestTo;
import by.shaminko.distcomp.dto.UserResponseTo;
import by.shaminko.distcomp.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping({"/api/v2.0/users", "/api/v2.0/creators"})
@RequiredArgsConstructor
public class UserV2Controller {
    private final UserService userService;

    @GetMapping
    public Collection<UserResponseTo> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserResponseTo getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseTo register(@RequestBody @Valid UserRequestTo request) {
        return userService.create(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.isSelf(#request.id, authentication)")
    public UserResponseTo update(@RequestBody @Valid UserRequestTo request) {
        return userService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @ownershipService.isSelf(#id, authentication)")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
