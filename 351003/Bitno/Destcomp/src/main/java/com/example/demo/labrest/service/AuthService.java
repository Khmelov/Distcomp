package com.example.demo.labrest.service;

import com.example.demo.labrest.model.Creator;
import com.example.demo.labrest.repository.CreatorRepository;
import com.example.demo.labrest.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CreatorRepository creatorRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public String authenticate(String login, String password) {
        Creator creator = creatorRepo.findByLogin(login)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(password, creator.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtTokenProvider.generateToken(creator.getLogin(), creator.getRole());
    }

    public boolean isCurrentUser(Long creatorId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return false;
        }
        String currentLogin = auth.getName();
        return creatorRepo.findByLogin(currentLogin)
                .map(creator -> creator.getId().equals(creatorId))
                .orElse(false);
    }

    public boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadCredentialsException("User not authenticated");
        }
        String login = auth.getName();
        return creatorRepo.findByLogin(login)
                .map(Creator::getId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
    }
}