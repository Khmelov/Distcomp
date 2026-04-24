package by.distcomp.app.service;

import by.distcomp.app.dto.UserRequestTo;
import by.distcomp.app.dto.UserResponseTo;
import by.distcomp.app.exception.DuplicateEntityException;
import by.distcomp.app.exception.ResourceNotFoundException;
import by.distcomp.app.mapper.UserMapper;
import by.distcomp.app.model.Article;
import by.distcomp.app.model.User;
import by.distcomp.app.repository.ArticleRepository;
import by.distcomp.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RestClient discussionClient;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       ArticleRepository articleRepository,
                       RestClient discussionClient) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.articleRepository = articleRepository;
        this.discussionClient = discussionClient;
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<Article> userArticles = articleRepository.findAll().stream()
                .filter(a -> a.getUser() != null && a.getUser().getId().equals(userId))
                .toList();

        for (Article article : userArticles) {
            try {
                discussionClient.delete()
                        .uri("/article/{articleId}", article.getId())
                        .retrieve()
                        .toBodilessEntity();
            } catch (Exception e) {
            }


            article.clearStickers();
            articleRepository.delete(article);
        }

        userRepository.delete(user);
    }

    @Transactional
    public UserResponseTo createUser(UserRequestTo dto) {
        if (userRepository.existsByLogin(dto.login())) {
            throw new DuplicateEntityException("login", dto.login());
        }
        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponseTo updateUser(Long id, UserRequestTo dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (dto.login() != null && !dto.login().equals(user.getLogin())) {
            if (userRepository.existsByLogin(dto.login())) {
                throw new DuplicateEntityException("login", dto.login());
            }
            user.setLogin(dto.login());
        }

        if (dto.firstname() != null) user.setFirstname(dto.firstname());
        if (dto.lastname() != null) user.setLastname(dto.lastname());
        if (dto.password() != null) user.setPassword(dto.password());

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    public List<UserResponseTo> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponseTo getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }

    public List<UserResponseTo> getUsersPage(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse)
                .getContent();
    }
}