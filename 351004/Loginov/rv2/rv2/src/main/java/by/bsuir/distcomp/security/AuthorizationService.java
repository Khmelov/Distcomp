package by.bsuir.distcomp.security;

import by.bsuir.distcomp.exception.ApiException;
import by.bsuir.distcomp.model.Issue;
import by.bsuir.distcomp.model.UserRole;
import by.bsuir.distcomp.model.Writer;
import by.bsuir.distcomp.repository.IssueRepository;
import by.bsuir.distcomp.repository.WriterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    private final WriterRepository writerRepository;
    private final IssueRepository issueRepository;

    public AuthorizationService(WriterRepository writerRepository, IssueRepository issueRepository) {
        this.writerRepository = writerRepository;
        this.issueRepository = issueRepository;
    }

    public Writer current(Authentication authentication) {
        return writerRepository.findByLogin(authentication.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "40103", "Current user not found"));
    }

    public boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + UserRole.ADMIN.name()));
    }

    public void requireAdmin(Authentication authentication) {
        if (!isAdmin(authentication)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "40305", "Access denied");
        }
    }

    public void requireSelfOrAdmin(Authentication authentication, Long writerId) {
        if (isAdmin(authentication)) {
            return;
        }
        if (!current(authentication).getId().equals(writerId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "40306", "Only own profile is writable");
        }
    }

    public void requireIssueOwnerOrAdmin(Authentication authentication, Long issueId) {
        if (isAdmin(authentication)) {
            return;
        }
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "40402", "Issue not found"));
        if (!issue.getWriter().getLogin().equals(authentication.getName())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "40307", "Only own content is writable");
        }
    }
}
