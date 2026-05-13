package org.rv.lab1.security;

import org.rv.lab1.domain.Editor;
import org.rv.lab1.domain.EditorRole;
import org.rv.lab1.domain.Story;
import org.rv.lab1.exception.ApiException;
import org.rv.lab1.service.CommentService;
import org.rv.lab1.service.EditorService;
import org.rv.lab1.service.StoryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class V2AccessControl {
    private final StoryService storyService;
    private final CommentService commentService;
    private final EditorService editorService;

    public V2AccessControl(StoryService storyService, CommentService commentService, EditorService editorService) {
        this.storyService = storyService;
        this.commentService = commentService;
        this.editorService = editorService;
    }

    public EditorPrincipal requireUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken
                || !(auth.getPrincipal() instanceof EditorPrincipal p)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, 1, "Authentication required");
        }
        return p;
    }

    public void requireAdmin() {
        if (requireUser().role() != EditorRole.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, 1, "Admin only");
        }
    }

    public void requireEditorWrite(long editorId) {
        EditorPrincipal p = requireUser();
        if (p.role() == EditorRole.ADMIN) {
            return;
        }
        if (p.editorId() != editorId) {
            throw new ApiException(HttpStatus.FORBIDDEN, 2, "Cannot modify another editor");
        }
    }

    public void requireStoryWrite(long storyId) {
        Story story = storyService.findEntity(storyId);
        requireStoryOwnedOrAdmin(story);
    }

    public void requireStoryOwnedOrAdmin(Story story) {
        EditorPrincipal p = requireUser();
        if (p.role() == EditorRole.ADMIN) {
            return;
        }
        Editor owner = story.getEditor();
        if (owner == null || owner.getId() == null || owner.getId() != p.editorId()) {
            throw new ApiException(HttpStatus.FORBIDDEN, 3, "Cannot modify this story");
        }
    }

    public void requireStoryCreate(long editorIdInRequest) {
        EditorPrincipal p = requireUser();
        if (p.role() == EditorRole.ADMIN) {
            editorService.findEntity(editorIdInRequest);
            return;
        }
        if (p.editorId() != editorIdInRequest) {
            throw new ApiException(HttpStatus.FORBIDDEN, 4, "Cannot create story for another editor");
        }
    }

    public void requireMarkerWrite() {
        requireAdmin();
    }

    public void requireCommentWriteForStory(long storyId) {
        Story story = storyService.findEntity(storyId);
        requireStoryOwnedOrAdmin(story);
    }

    public void requireCommentMutate(long commentId) {
        var existing = commentService.getById(commentId);
        requireCommentWriteForStory(existing.storyId());
    }

    public void requireStoryUpdateBody(long storyId, org.rv.lab1.dto.StoryRequestTo request) {
        requireStoryWrite(storyId);
        EditorPrincipal p = requireUser();
        if (p.role() == EditorRole.ADMIN) {
            return;
        }
        if (request.editorId() != p.editorId()) {
            throw new ApiException(HttpStatus.FORBIDDEN, 5, "editorId must match your account");
        }
    }
}
