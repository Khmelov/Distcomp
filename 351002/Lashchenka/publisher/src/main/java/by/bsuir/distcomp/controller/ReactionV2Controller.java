package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.exception.ForbiddenException;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.reaction.DiscussionReactionClient;
import by.bsuir.distcomp.reaction.ReactionKafkaGateway;
import by.bsuir.distcomp.repository.TweetRepository;
import by.bsuir.distcomp.security.EditorAuthPrincipal;
import by.bsuir.distcomp.security.V2Security;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/reactions")
public class ReactionV2Controller {

    private final ReactionKafkaGateway reactionKafkaGateway;
    private final DiscussionReactionClient discussionReactionClient;
    private final TweetRepository tweetRepository;

    public ReactionV2Controller(
            ReactionKafkaGateway reactionKafkaGateway,
            DiscussionReactionClient discussionReactionClient,
            TweetRepository tweetRepository) {
        this.reactionKafkaGateway = reactionKafkaGateway;
        this.discussionReactionClient = discussionReactionClient;
        this.tweetRepository = tweetRepository;
    }

    @PostMapping
    public ResponseEntity<ReactionResponseTo> create(@Valid @RequestBody ReactionRequestTo dto) {
        V2Security.currentEditor();
        return reactionKafkaGateway.create(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReactionResponseTo> getById(@PathVariable Long id) {
        V2Security.currentEditor();
        return reactionKafkaGateway.getById(id);
    }

    @GetMapping
    public ResponseEntity<List<ReactionResponseTo>> getAll() {
        V2Security.currentEditor();
        return reactionKafkaGateway.getAll();
    }

    @PutMapping
    public ResponseEntity<ReactionResponseTo> update(@Valid @RequestBody ReactionRequestTo dto) {
        ensureReactionModerationAccess(dto.getId());
        return reactionKafkaGateway.update(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        ensureReactionModerationAccess(id);
        return reactionKafkaGateway.deleteById(id);
    }

    private void ensureReactionModerationAccess(long reactionId) {
        EditorAuthPrincipal p = V2Security.currentEditor();
        if (V2Security.isAdmin(p)) {
            return;
        }
        ReactionResponseTo reaction = discussionReactionClient.getById(reactionId);
        var tweet = tweetRepository.findById(reaction.getTweetId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + reaction.getTweetId() + " not found", 40405));
        if (!tweet.getEditorId().equals(p.getEditorId())) {
            throw new ForbiddenException("Not allowed to modify this reaction", 40305);
        }
    }
}
