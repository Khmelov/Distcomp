package com.task310.blogplatform.service;

import com.task310.blogplatform.model.Article;
import com.task310.blogplatform.model.Role;
import com.task310.blogplatform.model.User;
import com.task310.blogplatform.repository.ArticleRepository;
import com.task310.blogplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ArticleRepository articleRepository;

    public String getCurrentUserLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    public User getCurrentUser() {
        String login = getCurrentUserLogin();
        if (login != null) {
            return userRepository.findByLogin(login).orElse(null);
        }
        return null;
    }

    public Role getCurrentUserRole() {
        User user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }

    public boolean isAdmin() {
        return Role.ADMIN.equals(getCurrentUserRole());
    }

    public boolean isCurrentUser(Long userId) {
        User currentUser = getCurrentUser();
        return currentUser != null && currentUser.getId().equals(userId);
    }

    public boolean isArticleOwner(Long articleId) {
        if (isAdmin()) {
            return true;
        }
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        Article article = articleRepository.findById(articleId).orElse(null);
        if (article == null || article.getUser() == null) {
            return false;
        }
        return currentUser.getId().equals(article.getUser().getId());
    }
}

