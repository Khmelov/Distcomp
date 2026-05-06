package com.example.task310.security;

import com.example.task310.dto.PostResponseTo;
import com.example.task310.entity.Issue;
import com.example.task310.entity.Writer;
import com.example.task310.repository.IssueRepository;
import com.example.task310.repository.WriterRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component("accessGuard")
public class AccessGuard {
    private final WriterRepository writerRepository;
    private final IssueRepository issueRepository;
    private final RestClient restClient;

    public AccessGuard(WriterRepository writerRepository, IssueRepository issueRepository, RestClient restClient) {
        this.writerRepository = writerRepository;
        this.issueRepository = issueRepository;
        this.restClient = restClient;
    }

    public boolean canManageWriter(Long writerId, Authentication authentication) {
        if (writerId == null || authentication == null) {
            return false;
        }
        if (isAdmin(authentication)) {
            return true;
        }
        Writer current = currentWriter(authentication);
        return current != null && writerId.equals(current.getId());
    }

    public boolean canUseWriter(Long writerId, Authentication authentication) {
        return canManageWriter(writerId, authentication);
    }

    public boolean canManageIssue(Long issueId, Authentication authentication) {
        if (issueId == null || authentication == null) {
            return false;
        }
        if (isAdmin(authentication)) {
            return true;
        }
        Writer current = currentWriter(authentication);
        if (current == null) {
            return false;
        }
        Issue issue = issueRepository.findById(issueId).orElse(null);
        return issue != null && current.getId().equals(issue.getWriterId());
    }

    public boolean canManageIssueByWriter(Long writerId, Authentication authentication) {
        return canUseWriter(writerId, authentication);
    }

    public boolean canManagePostByIssue(Long issueId, Authentication authentication) {
        return canManageIssueByWriter(resolveIssueOwnerId(issueId), authentication);
    }

    public boolean canManagePostById(Long postId, Authentication authentication) {
        if (postId == null || authentication == null) {
            return false;
        }
        if (isAdmin(authentication)) {
            return true;
        }
        try {
            PostResponseTo post = restClient.get()
                    .uri("/{id}", postId)
                    .retrieve()
                    .body(PostResponseTo.class);
            if (post == null) {
                return false;
            }
            return canManagePostByIssue(post.issueId(), authentication);
        } catch (Exception e) {
            return false;
        }
    }

    private Long resolveIssueOwnerId(Long issueId) {
        if (issueId == null) {
            return null;
        }
        return issueRepository.findById(issueId).map(Issue::getWriterId).orElse(null);
    }

    private Writer currentWriter(Authentication authentication) {
        return writerRepository.findByLogin(authentication.getName()).orElse(null);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
