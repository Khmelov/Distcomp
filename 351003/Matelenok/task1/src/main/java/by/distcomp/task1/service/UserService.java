package by.distcomp.task1.service;

import by.distcomp.task1.dto.UserResponseTo;
import by.distcomp.task1.dto.UserRequestTo;
import by.distcomp.task1.exception.ResourceNotFoundException;
import by.distcomp.task1.mapper.UserMapper;
import by.distcomp.task1.model.Article;
import by.distcomp.task1.model.Note;
import by.distcomp.task1.model.Sticker;
import by.distcomp.task1.model.User;
import by.distcomp.task1.repository.ArticleRepository;
import by.distcomp.task1.repository.NoteRepository;
import by.distcomp.task1.repository.UserRepository;
import org.springframework.stereotype.Service;

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
    public UserResponseTo createUser(UserRequestTo dto) {
        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }
    public UserResponseTo updateUser(Long id, UserRequestTo dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (dto.login() != null) {
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
}
