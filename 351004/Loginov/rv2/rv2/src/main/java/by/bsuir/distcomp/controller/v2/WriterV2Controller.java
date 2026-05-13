package by.bsuir.distcomp.controller.v2;

import by.bsuir.distcomp.dto.WriterDto;
import by.bsuir.distcomp.security.AuthService;
import by.bsuir.distcomp.security.AuthorizationService;
import by.bsuir.distcomp.service.WriterService;
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
@RequestMapping("/api/v2.0/writers")
public class WriterV2Controller {
    private final WriterService writerService;
    private final AuthService authService;
    private final AuthorizationService authorization;

    public WriterV2Controller(WriterService writerService, AuthService authService, AuthorizationService authorization) {
        this.writerService = writerService;
        this.authService = authService;
        this.authorization = authorization;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WriterDto create(@Valid @RequestBody WriterDto dto) {
        return authService.register(dto);
    }

    @GetMapping("/{id}")
    public WriterDto get(@PathVariable Long id) {
        return writerService.get(id);
    }

    @GetMapping
    public List<WriterDto> findAll(@PageableDefault(size = 50, sort = "id") Pageable pageable) {
        return writerService.findAll(pageable);
    }

    @GetMapping("/me")
    public WriterDto me(Authentication authentication) {
        return writerService.get(authorization.current(authentication).getId());
    }

    @PutMapping("/{id}")
    public WriterDto update(@PathVariable Long id, @Valid @RequestBody WriterDto dto, Authentication authentication) {
        authorization.requireSelfOrAdmin(authentication, id);
        return writerService.update(id, dto);
    }

    @PutMapping
    public WriterDto update(@Valid @RequestBody WriterDto dto, Authentication authentication) {
        authorization.requireSelfOrAdmin(authentication, dto.id());
        return writerService.update(dto.id(), dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        authorization.requireSelfOrAdmin(authentication, id);
        writerService.delete(id);
    }
}
