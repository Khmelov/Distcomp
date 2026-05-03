package org.example.newsapi.security;

import lombok.RequiredArgsConstructor;
import org.example.newsapi.entity.User;
import org.example.newsapi.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    public boolean isOwner(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        String currentLogin = auth.getName();
        User currentUser = userRepository.findByLogin(currentLogin).orElse(null);
        return currentUser != null && currentUser.getId().equals(userId);
    }
}