package com.lizaveta.notebook.service;

import com.lizaveta.notebook.exception.ForbiddenException;
import com.lizaveta.notebook.exception.ResourceNotFoundException;
import com.lizaveta.notebook.model.UserRole;
import com.lizaveta.notebook.model.entity.Story;
import com.lizaveta.notebook.repository.StoryRepository;
import com.lizaveta.notebook.security.SecurityWriter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class V2AuthorizationService {

    private static final String STORY_NOT_FOUND = "Story not found with id: ";
    private final StoryRepository storyRepository;

    public V2AuthorizationService(final StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public SecurityWriter getCurrent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof SecurityWriter writer)) {
            throw new IllegalStateException("Unauthenticated v2.0 call");
        }
        return writer;
    }

    public long getCurrentWriterId() {
        return getCurrent().getWriterId();
    }

    public boolean isAdmin() {
        return getCurrent().getUserRole() == UserRole.ADMIN;
    }

    public void requireAdmin() {
        if (!isAdmin()) {
            throw new ForbiddenException("The operation requires the ADMIN role");
        }
    }

    public void requireAdminForMarkerWrite() {
        requireAdmin();
    }

    public void requireAdminOrWriterOwn(final long writerId) {
        if (isAdmin()) {
            return;
        }
        if (getCurrent().getWriterId() == writerId) {
            return;
        }
        throw new ForbiddenException("This action is only allowed for the writer that owns the resource");
    }

    public void requireAdminOrOwnStory(final long storyId) {
        if (isAdmin()) {
            return;
        }
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException(STORY_NOT_FOUND + storyId));
        if (getCurrent().getWriterId() == story.getWriterId()) {
            return;
        }
        throw new ForbiddenException("This action is only allowed for the owner of the story");
    }
}
