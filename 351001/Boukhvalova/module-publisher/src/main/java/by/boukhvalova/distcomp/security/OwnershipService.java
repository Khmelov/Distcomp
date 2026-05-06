package by.boukhvalova.distcomp.security;

import by.boukhvalova.distcomp.security.user.AuthenticatedUser;
import by.shaminko.distcomp.services.NoteService;
import by.boukhvalova.distcomp.repositories.TweetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("ownershipService")
@RequiredArgsConstructor
public class OwnershipService {
    private final TweetRepository tweetRepository;
    private final NoteService noteService;

    public boolean isSelf(long userId, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return user.getId() == userId;
    }

    public boolean isTweetOwner(long tweetId, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return tweetRepository.findById(tweetId)
                .map(t -> t.getUserId() == user.getId())
                .orElse(false);
    }

    public boolean isNoteOwner(long noteId, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        try {
            return noteService.getById(noteId).getUserId() == user.getId();
        } catch (RuntimeException ex) {
            return false;
        }
    }
}
