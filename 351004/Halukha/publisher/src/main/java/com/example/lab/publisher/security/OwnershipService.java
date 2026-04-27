package com.example.lab.publisher.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.lab.publisher.model.News;
import com.example.lab.publisher.model.User;
import com.example.lab.publisher.repository.NewsRepository;
import com.example.lab.publisher.repository.UserRepository;

@Service("ownership")
public class OwnershipService {

    private final UserRepository userRepository;
    private final NewsRepository newsRepository;

    public OwnershipService(UserRepository userRepository, NewsRepository newsRepository) {
        this.userRepository = userRepository;
        this.newsRepository = newsRepository;
    }

    public boolean canAccessUser(Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return false;

        return userRepository.findByLogin(auth.getName())
                .map(user -> user.getId().equals(id))
                .orElse(false);
    }

    public boolean canModifyNews(Long newsId) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByLogin(login).orElse(null);
        if (user == null || user.getId() == null) {
            return false;
        }
        News news = newsRepository.findById(newsId).orElse(null);
        return news != null && user.getId().equals(news.getUserId());
    }

    public Long currentUserId() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByLogin(login).map(User::getId).orElse(null);
    }
}
