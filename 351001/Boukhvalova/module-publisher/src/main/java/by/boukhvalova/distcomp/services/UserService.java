package by.boukhvalova.distcomp.services;

import by.boukhvalova.distcomp.dto.UserMapper;
import by.boukhvalova.distcomp.dto.UserRequestTo;
import by.boukhvalova.distcomp.dto.UserResponseTo;
import by.boukhvalova.distcomp.entities.UserRole;
import by.boukhvalova.distcomp.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    public final UserRepository repImpl;
    public final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;


    public List<UserResponseTo> getAll() {
        return repImpl.getAll().map(mapper::out).toList();
    }

    @Cacheable(value = "users", key = "#id")
    public UserResponseTo getById(Long id) {
        return repImpl.get(id).map(mapper::out).orElseThrow();
    }

    @CachePut(value = "users", key = "#req.id")
    public UserResponseTo create(UserRequestTo req) {
        var entity = mapper.in(req);
        entity.setRole(entity.getRole() == null ? UserRole.CUSTOMER : entity.getRole());
        entity.setPassword(encodeIfNeeded(entity.getPassword()));
        return repImpl.create(entity).map(mapper::out).orElseThrow();
    }

    @CachePut(value = "users", key = "#req.id")
    public UserResponseTo update(UserRequestTo req) {
        var existing = repImpl.findById(req.getId()).orElseThrow();
        var entity = mapper.in(req);
        entity.setRole(entity.getRole() == null ? existing.getRole() : entity.getRole());
        if (entity.getPassword() == null || entity.getPassword().isBlank()) {
            entity.setPassword(existing.getPassword());
        } else {
            entity.setPassword(encodeIfNeeded(entity.getPassword()));
        }
        return repImpl.update(entity).map(mapper::out).orElseThrow();
    }

    @CacheEvict(value = "users", key = "#id")
    public void delete(Long id) {
        repImpl.delete(id);
    }

    private String encodeIfNeeded(String rawOrEncodedPassword) {
        if (rawOrEncodedPassword == null || rawOrEncodedPassword.isBlank()) {
            return rawOrEncodedPassword;
        }
        if (rawOrEncodedPassword.startsWith("$2")) {
            return rawOrEncodedPassword;
        }
        return passwordEncoder.encode(rawOrEncodedPassword);
    }
}
