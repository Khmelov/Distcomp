package by.bsuir.task361.publisher.service;

import by.bsuir.task361.publisher.entity.User;
import by.bsuir.task361.publisher.entity.UserRole;
import by.bsuir.task361.publisher.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final UserService userService;

    public CurrentUserService(UserService userService) {
        this.userService = userService;
    }

    public String getCurrentLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, 40101, "Authentication required");
        }
        return authentication.getName();
    }

    public UserRole getCurrentRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, 40101, "Authentication required");
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .findFirst()
                .map(authority -> UserRole.valueOf(authority.substring(5)))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, 40101, "Authentication required"));
    }

    public User getCurrentUser() {
        return userService.getUserByLogin(getCurrentLogin());
    }

    public boolean isAdmin() {
        return getCurrentRole() == UserRole.ADMIN;
    }
}
