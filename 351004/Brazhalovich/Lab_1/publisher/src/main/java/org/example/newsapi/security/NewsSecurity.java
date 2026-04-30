package org.example.newsapi.security;

import lombok.RequiredArgsConstructor;
import org.example.newsapi.entity.User;
import org.example.newsapi.repository.NewsRepository;
import org.example.newsapi.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("newsSecurity")
@RequiredArgsConstructor
public class NewsSecurity {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    public boolean isOwner(Long newsId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        String currentLogin = auth.getName();
        User currentUser = userRepository.findByLogin(currentLogin).orElse(null);
        if (currentUser == null) {
            return false;
        }
        return newsRepository.findById(newsId)
                .map(news -> news.getUser().getId().equals(currentUser.getId()))
                .orElse(false);
    }
}