package com.aitor.publisher.service;

import com.aitor.publisher.dto.UserRequestTo;
import com.aitor.publisher.dto.UserResponseTo;
import com.aitor.publisher.exception.EntityNotExistsException;
import com.aitor.publisher.model.User;
import com.aitor.publisher.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public UserResponseTo add(UserRequestTo requestBody){
        User persisted = repository.save(new User(
                requestBody.getLogin(),
                requestBody.getPassword(),
                requestBody.getFirstname(),
                requestBody.getLastname()));
        return toResponse(persisted);
    }

    public UserResponseTo set(Long id, UserRequestTo requestBody){
        var entity = getEntity(id);
        entity.setLogin(requestBody.getLogin());
        entity.setPassword(requestBody.getPassword());
        entity.setFirstname(requestBody.getFirstname());
        entity.setLastname(requestBody.getLastname());
        return toResponse(repository.save(entity));
    }

    public UserResponseTo get(Long id) {
        return toResponse(getEntity(id));
    }

    public List<UserResponseTo> getAll(){
        return repository.findAll().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());
    }

    public UserResponseTo remove(Long id) {
        var entityOptional = repository.findById(id);
        if (entityOptional.isPresent()) {
            var entity = entityOptional.get();
            var response = toResponse(entity);
            repository.delete(entity);
            return response;
        } else
            throw new EntityNotExistsException();
    }

    private User getEntity(Long id){
        var entity = repository.findById(id);
        if (entity.isPresent())
            return entity.get();
        throw new EntityNotExistsException();
    }

    private UserResponseTo toResponse(User entity){
        return new UserResponseTo(
                entity.getId(),
                entity.getLogin(),
                entity.getFirstname(),
                entity.getLastname());
    }
}
