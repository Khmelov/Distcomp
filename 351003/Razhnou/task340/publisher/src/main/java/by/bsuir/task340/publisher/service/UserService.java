package by.bsuir.task340.publisher.service;

import by.bsuir.task340.publisher.dto.request.UserRequestTo;
import by.bsuir.task340.publisher.dto.response.UserResponseTo;
import by.bsuir.task340.publisher.entity.User;
import by.bsuir.task340.publisher.exception.BadRequestException;
import by.bsuir.task340.publisher.exception.ConflictException;
import by.bsuir.task340.publisher.exception.NotFoundException;
import by.bsuir.task340.publisher.mapper.UserMapper;
import by.bsuir.task340.publisher.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponseTo create(UserRequestTo request) {
        if (request.id() != null) {
            throw new BadRequestException("User id must be null on create", 3);
        }
        validateLogin(request.login());
        validatePassword(request.password());
        validateHumanName(request.firstname(), "User firstname");
        validateHumanName(request.lastname(), "User lastname");
        validateUniqueLoginOnCreate(request.login());

        User user = UserMapper.toEntity(request);
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponseTo> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseTo findById(Long id) {
        validateId(id, "User id");
        return UserMapper.toResponse(getUser(id));
    }

    @Transactional
    public UserResponseTo update(UserRequestTo request) {
        validateId(request.id(), "User id");
        validateLogin(request.login());
        validatePassword(request.password());
        validateHumanName(request.firstname(), "User firstname");
        validateHumanName(request.lastname(), "User lastname");
        validateUniqueLoginOnUpdate(request.login(), request.id());

        User user = getUser(request.id());
        UserMapper.updateEntity(user, request);
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        validateId(id, "User id");
        userRepository.delete(getUser(id));
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found", 1));
    }

    private void validateUniqueLoginOnCreate(String login) {
        if (userRepository.existsByLogin(login.trim())) {
            throw new ConflictException("User login already exists", 1);
        }
    }

    private void validateUniqueLoginOnUpdate(String login, Long id) {
        if (userRepository.existsByLoginAndIdNot(login.trim(), id)) {
            throw new ConflictException("User login already exists", 1);
        }
    }

    private void validateId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new BadRequestException(fieldName + " must be greater than 0", 1);
        }
    }

    private void validateLogin(String login) {
        validateTextLength(login, "User login", 2, 64, 2);
    }

    private void validatePassword(String password) {
        validateTextLength(password, "User password", 8, 128, 4);
    }

    private void validateHumanName(String value, String fieldName) {
        validateTextLength(value, fieldName, 2, 64, 5);
    }

    private void validateTextLength(String value, String fieldName, int min, int max, int suffix) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " must not be blank", suffix);
        }
        int length = value.trim().length();
        if (length < min || length > max) {
            throw new BadRequestException(fieldName + " length must be between " + min + " and " + max, suffix);
        }
    }
}
