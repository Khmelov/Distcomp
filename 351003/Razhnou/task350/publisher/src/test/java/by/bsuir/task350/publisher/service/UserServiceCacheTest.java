package by.bsuir.task350.publisher.service;

import by.bsuir.task350.publisher.config.CacheNames;
import by.bsuir.task350.publisher.dto.request.UserRequestTo;
import by.bsuir.task350.publisher.dto.response.UserResponseTo;
import by.bsuir.task350.publisher.entity.User;
import by.bsuir.task350.publisher.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceCacheTest {
    @Mock
    private UserRepository userRepository;

    private PublisherCacheService cacheService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        cacheService = new PublisherCacheService(new ConcurrentMapCacheManager(CacheNames.USERS));
        userService = new UserService(userRepository, cacheService);
    }

    @Test
    void findByIdCachesValueAndSkipsRepositoryOnSecondRead() {
        User user = user(1L, "cachedLogin", "password123", "Ivan", "Ivanov");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseTo first = userService.findById(1L);
        UserResponseTo second = userService.findById(1L);

        assertEquals(first, second);
        assertEquals(first, cacheService.get(CacheNames.USERS, 1L, UserResponseTo.class));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void updateRefreshesCachedValue() {
        User stored = user(1L, "oldLogin", "password123", "Old", "Name");
        when(userRepository.existsByLoginAndIdNot("newLogin", 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(stored));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseTo response = userService.update(new UserRequestTo(1L, "newLogin", "password123", "New", "User"));

        assertEquals(response, cacheService.get(CacheNames.USERS, 1L, UserResponseTo.class));
        verify(userRepository).save(stored);
    }

    @Test
    void deleteEvictsCachedValue() {
        User stored = user(1L, "login", "password123", "Ivan", "Ivanov");
        cacheService.put(CacheNames.USERS, 1L, new UserResponseTo(1L, "login", "password123", "Ivan", "Ivanov"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(stored));

        userService.delete(1L);

        assertNull(cacheService.get(CacheNames.USERS, 1L, UserResponseTo.class));
        verify(userRepository).delete(stored);
    }

    private User user(Long id, String login, String password, String firstname, String lastname) {
        return new User(id, login, password, firstname, lastname);
    }
}
