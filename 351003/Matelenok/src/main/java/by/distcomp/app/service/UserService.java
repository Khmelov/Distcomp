package by.distcomp.app.service;

import by.distcomp.app.dto.UserResponseTo;
import by.distcomp.app.dto.UserRequestTo;
import by.distcomp.app.exception.DuplicateEntityException;
import by.distcomp.app.exception.ResourceNotFoundException;
import by.distcomp.app.mapper.UserMapper;
import by.distcomp.app.model.Article;
import by.distcomp.app.model.Note;
import by.distcomp.app.model.Sticker;
import by.distcomp.app.model.User;
import by.distcomp.app.repository.ArticleRepository;
import by.distcomp.app.repository.NoteRepository;
import by.distcomp.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final ArticleRepository articleRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper,
                       ArticleRepository articleRepository, NoteRepository noteRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.articleRepository = articleRepository;
        this.noteRepository = noteRepository;
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        for (Article article : articleRepository.findAll()) {
            if (article.getUser() != null && article.getUser().getId().equals(userId)) {

                for (Note note : noteRepository.findAll()) {
                    if (note.getArticle() != null &&
                            note.getArticle().getId().equals(article.getId())) {
                        noteRepository.deleteById(note.getId());
                    }
                }

                for (Sticker sticker : article.getStickers()) {
                    sticker.getArticles().remove(article);
                }
                article.getStickers().clear();

                articleRepository.deleteById(article.getId());
            }
        }

        userRepository.deleteById(userId);
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
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));


        if (dto.login() != null && !dto.login().equals(user.getLogin())) {
            if (userRepository.existsByLogin(dto.login())) {
                throw new DuplicateEntityException("login", dto.login());
            }
            user.setLogin(dto.login());
        }

        if (dto.firstname() != null) {
            user.setFirstname(dto.firstname());
        }
        if (dto.lastname() != null) {
            user.setLastname(dto.lastname());
        }
        if (dto.password() != null) {
            user.setPassword(dto.password());
        }

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    public List<UserResponseTo> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponseTo getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
        return userMapper.toResponse(user);
    }

    public List<UserResponseTo> getUsersPage(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse)
                .getContent();
    }
}