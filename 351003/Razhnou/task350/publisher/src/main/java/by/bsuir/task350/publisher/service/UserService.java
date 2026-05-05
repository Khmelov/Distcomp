package by.bsuir.task350.publisher.service;

import by.bsuir.task350.publisher.config.CacheNames;
import by.bsuir.task350.publisher.dto.request.UserRequestTo;
import by.bsuir.task350.publisher.dto.response.UserResponseTo;
import by.bsuir.task350.publisher.entity.User;
import by.bsuir.task350.publisher.exception.BadRequestException;
import by.bsuir.task350.publisher.exception.ConflictException;
import by.bsuir.task350.publisher.exception.NotFoundException;
import by.bsuir.task350.publisher.mapper.UserMapper;
import by.bsuir.task350.publisher.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PublisherCacheService cacheService;

    public UserService(UserRepository userRepository, PublisherCacheService cacheService) {
        this.userRepository = userRepository;
        this.cacheService = cacheService;
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
        UserResponseTo response = UserMapper.toResponse(userRepository.save(user));
        cacheService.put(CacheNames.USERS, response.id(), response);
        return response;
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
        UserResponseTo cached = cacheService.get(CacheNames.USERS, id, UserResponseTo.class);
        if (cached != null) {
            return cached;
        }
        UserResponseTo response = UserMapper.toResponse(getUser(id));
        cacheService.put(CacheNames.USERS, id, response);
        return response;
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
        UserResponseTo response = UserMapper.toResponse(userRepository.save(user));
        cacheService.put(CacheNames.USERS, response.id(), response);
        return response;
    }

    @Transactional
    public void delete(Long id) {
        validateId(id, "User id");
        userRepository.delete(getUser(id));
        cacheService.evict(CacheNames.USERS, id);
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
