package by.bsuir.distcomp.controller.v2;

import by.bsuir.distcomp.core.service.AuthorService;
import by.bsuir.distcomp.dto.response.AuthorResponseTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/authors")
public class AuthorRestControllerV2 {

    private final AuthorService authorService;

    public AuthorRestControllerV2(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuthorResponseTo> getAll() {
        return authorService.getAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @securityService.isAuthorOwner(#id, principal))")
    public void delete(@PathVariable Long id) {
        authorService.deleteById(id);
    }
}