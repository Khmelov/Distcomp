package by.boukhvalova.distcomp.controllers.v2;

import by.boukhvalova.distcomp.dto.StickerRequestTo;
import by.boukhvalova.distcomp.dto.StickerResponseTo;
import by.boukhvalova.distcomp.services.StickerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping({"/api/v2.0/stickers", "/api/v2.0/markers", "/api/v2.0/labels"})
@RequiredArgsConstructor
public class StickerV2Controller {
    private final StickerService stickerService;

    @GetMapping
    public Collection<StickerResponseTo> getAll() {
        return stickerService.getAll();
    }

    @GetMapping("/{id}")
    public StickerResponseTo getById(@PathVariable Long id) {
        return stickerService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public StickerResponseTo create(@RequestBody @Valid StickerRequestTo request) {
        return stickerService.create(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public StickerResponseTo update(@RequestBody @Valid StickerRequestTo request) {
        return stickerService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        stickerService.delete(id);
    }
}
