package by.bsuir.publisher.security;

import org.springframework.stereotype.Component;

@Component("commentSecurity")
public class CommentSecurity {

    /**
     * Comment entity in publisher cache does not store author/login,
     * so ownership cannot be determined. Non-admins are always rejected;
     * the @PreAuthorize chain should include hasRole('ADMIN') first.
     */
    public boolean isOwnerById(Long commentId, String login) {
        return false;
    }
}
