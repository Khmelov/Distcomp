package by.shaminko.distcomp.security;

import by.shaminko.distcomp.security.user.AuthenticatedUser;
import by.shaminko.distcomp.services.ReactionService;
import by.shaminko.distcomp.repositories.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("ownershipService")
@RequiredArgsConstructor
public class OwnershipService {
    private final TweetRepository tweetRepository;
    private final ReactionService reactionService;

    public boolean isSelf(long creatorId, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return user.getId() == creatorId;
    }

    public boolean isTweetOwner(long tweetId, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return tweetRepository.findById(tweetId)
                .map(t -> t.getCreatorId() == user.getId())
                .orElse(false);
    }

    public boolean isReactionOwner(long reactionId, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        try {
            return reactionService.getById(reactionId).getCreatorId() == user.getId();
        } catch (RuntimeException ex) {
            return false;
        }
    }
}
