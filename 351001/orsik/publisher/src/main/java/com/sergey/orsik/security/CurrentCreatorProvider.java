package com.sergey.orsik.security;

import com.sergey.orsik.entity.Creator;
import com.sergey.orsik.repository.CreatorRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
public class CurrentCreatorProvider {

    private final CreatorRepository creatorRepository;

    public CurrentCreatorProvider(CreatorRepository creatorRepository) {
        this.creatorRepository = creatorRepository;
    }

    public Creator requireCurrentCreator() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(UNAUTHORIZED, "Authentication is required");
        }
        return creatorRepository.findByLogin(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Current user not found"));
    }
}
