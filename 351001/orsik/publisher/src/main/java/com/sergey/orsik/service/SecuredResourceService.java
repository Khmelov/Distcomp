package com.sergey.orsik.service;

import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.request.TweetRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import com.sergey.orsik.dto.response.CreatorResponseTo;
import com.sergey.orsik.dto.response.TweetResponseTo;
import com.sergey.orsik.entity.Creator;
import com.sergey.orsik.entity.CreatorRole;
import com.sergey.orsik.security.CurrentCreatorProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SecuredResourceService {

    private final CurrentCreatorProvider currentCreatorProvider;
    private final CreatorService creatorService;
    private final TweetService tweetService;
    private final CommentService commentService;

    public SecuredResourceService(
            CurrentCreatorProvider currentCreatorProvider,
            CreatorService creatorService,
            TweetService tweetService,
            CommentService commentService) {
        this.currentCreatorProvider = currentCreatorProvider;
        this.creatorService = creatorService;
        this.tweetService = tweetService;
        this.commentService = commentService;
    }

    public Creator requireCurrentCreator() {
        return currentCreatorProvider.requireCurrentCreator();
    }

    public CreatorResponseTo updateCreator(Long id, com.sergey.orsik.dto.request.CreatorRequestTo request) {
        Creator current = requireCurrentCreator();
        if (!isAdmin(current) && !current.getId().equals(id)) {
            throw forbidden("CUSTOMER can update only own profile");
        }
        if (!isAdmin(current) && request.getRole() != null && request.getRole() != CreatorRole.CUSTOMER) {
            throw forbidden("CUSTOMER cannot escalate role");
        }
        if (!isAdmin(current)) {
            request.setRole(CreatorRole.CUSTOMER);
        }
        return creatorService.update(id, request);
    }

    public void deleteCreator(Long id) {
        Creator current = requireCurrentCreator();
        if (!isAdmin(current) && !current.getId().equals(id)) {
            throw forbidden("CUSTOMER can delete only own profile");
        }
        creatorService.deleteById(id);
    }

    public TweetResponseTo createTweet(TweetRequestTo request) {
        Creator current = requireCurrentCreator();
        if (!isAdmin(current)) {
            request.setCreatorId(current.getId());
        }
        return tweetService.create(request);
    }

    public TweetResponseTo updateTweet(Long id, TweetRequestTo request) {
        Creator current = requireCurrentCreator();
        TweetResponseTo existing = tweetService.findById(id);
        if (!isAdmin(current) && !current.getId().equals(existing.getCreatorId())) {
            throw forbidden("CUSTOMER can update only own tweets");
        }
        if (!isAdmin(current)) {
            request.setCreatorId(current.getId());
        }
        return tweetService.update(id, request);
    }

    public void deleteTweet(Long id) {
        Creator current = requireCurrentCreator();
        TweetResponseTo existing = tweetService.findById(id);
        if (!isAdmin(current) && !current.getId().equals(existing.getCreatorId())) {
            throw forbidden("CUSTOMER can delete only own tweets");
        }
        tweetService.deleteById(id);
    }

    public CommentResponseTo createComment(CommentRequestTo request) {
        Creator current = requireCurrentCreator();
        if (!isAdmin(current)) {
            request.setCreatorId(current.getId());
        }
        return commentService.create(request);
    }

    public CommentResponseTo updateComment(Long id, CommentRequestTo request) {
        Creator current = requireCurrentCreator();
        CommentResponseTo existing = commentService.findById(id);
        if (!isAdmin(current) && !current.getId().equals(existing.getCreatorId())) {
            throw forbidden("CUSTOMER can update only own comments");
        }
        if (!isAdmin(current)) {
            request.setCreatorId(current.getId());
        }
        return commentService.update(id, request);
    }

    public void deleteComment(Long id) {
        Creator current = requireCurrentCreator();
        CommentResponseTo existing = commentService.findById(id);
        if (!isAdmin(current) && !current.getId().equals(existing.getCreatorId())) {
            throw forbidden("CUSTOMER can delete only own comments");
        }
        commentService.deleteById(id);
    }

    public void requireAdmin() {
        Creator current = requireCurrentCreator();
        if (!isAdmin(current)) {
            throw forbidden("ADMIN role required");
        }
    }

    private static boolean isAdmin(Creator current) {
        return current.getRole() == CreatorRole.ADMIN;
    }

    private static ResponseStatusException forbidden(String message) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, message);
    }
}
