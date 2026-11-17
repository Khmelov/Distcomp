package by.bsuir.rest.service.impl;

import by.bsuir.rest.cache.ResponseCache;
import by.bsuir.rest.dto.request.UserRequestTo;
import by.bsuir.rest.dto.response.UserResponseTo;
import by.bsuir.rest.entity.User;
import by.bsuir.rest.exception.ApiException;
import by.bsuir.rest.exception.ErrorCode;
import by.bsuir.rest.mapper.UserMapper;
import by.bsuir.rest.repository.PageQuery;
import by.bsuir.rest.repository.UserFilter;
import by.bsuir.rest.repository.UserRepository;
import by.bsuir.rest.service.RequestValidator;
import by.bsuir.rest.service.UpdateIdResolver;
import by.bsuir.rest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final RequestValidator requestValidator;
    private final UpdateIdResolver updateIdResolver;
    private final ResponseCache cache;

    @Override
    public UserResponseTo create(UserRequestTo request) {
        requestValidator.validate(request);
        ensureLoginAvailable(request.getLogin(), null);
        User user = mapper.toEntity(request);
        user.setId(null);
        UserResponseTo response = mapper.toResponse(repository.save(user));
        cache.put(ResponseCache.USER, response.getId(), response);
        cache.evict(ResponseCache.USER, ResponseCache.ALL);
        return response;
    }

    @Override
    public UserResponseTo getById(Long id) {
        return cache.get(ResponseCache.USER, id, UserResponseTo.class)
                .orElseGet(() -> {
                    UserResponseTo response = mapper.toResponse(find(id));
                    cache.put(ResponseCache.USER, id, response);
                    return response;
                });
    }

    @Override
    public List<UserResponseTo> getAll() {
        Optional<List<UserResponseTo>> cached = cache.getList(ResponseCache.USER, ResponseCache.ALL, UserResponseTo.class);
        if (cached.isPresent()) {
            return cached.get();
        }
        List<UserResponseTo> responses = repository.find(UserFilter.empty(), PageQuery.all()).content().stream()
                .map(mapper::toResponse)
                .toList();
        cache.put(ResponseCache.USER, ResponseCache.ALL, responses);
        return responses;
    }

    @Override
    public UserResponseTo update(Long pathId, UserRequestTo request) {
        requestValidator.validate(request);
        Long id = updateIdResolver.resolve(pathId, request.getId());
        User user = find(id);
        ensureLoginAvailable(request.getLogin(), id);
        mapper.update(request, user);
        UserResponseTo response = mapper.toResponse(user);
        cache.put(ResponseCache.USER, response.getId(), response);
        cache.evict(ResponseCache.USER, ResponseCache.ALL);
        return response;
    }

    @Override
    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw ApiException.notFound("User", id);
        }
        cache.evict(ResponseCache.USER, id);
        cache.evict(ResponseCache.USER, ResponseCache.ALL);
    }

    private User find(Long id) {
        return repository.findById(id).orElseThrow(() -> ApiException.notFound("User", id));
    }

    private void ensureLoginAvailable(String login, Long currentId) {
        boolean taken = repository.find(new UserFilter(login, null, null), PageQuery.all()).content().stream()
                .anyMatch(user -> login.equals(user.getLogin()) && !user.getId().equals(currentId));
        if (taken) {
            throw new ApiException(HttpStatus.FORBIDDEN, ErrorCode.DUPLICATE, "User login must be unique");
        }
    }
}
