package by.bsuir.distcomp.controller.v2;

import by.bsuir.distcomp.core.service.TweetService;
import by.bsuir.distcomp.dto.request.TweetRequestTo;
import by.bsuir.distcomp.dto.response.TweetResponseTo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/tweets")
public class TweetRestControllerV2 {

    private final TweetService tweetService;

    public TweetRestControllerV2(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping
    public List<TweetResponseTo> getAll() {
        return tweetService.search(null, null, null, null, null, null);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo request) {
        return new ResponseEntity<>(tweetService.create(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @securityService.isTweetOwner(#id, principal))")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tweetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}