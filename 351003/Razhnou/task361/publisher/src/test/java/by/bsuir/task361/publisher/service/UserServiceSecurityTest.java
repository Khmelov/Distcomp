package by.bsuir.task361.publisher.service;

import by.bsuir.task361.publisher.config.CacheNames;
import by.bsuir.task361.publisher.dto.request.UserRequestTo;
import by.bsuir.task361.publisher.dto.response.UserResponseTo;
import by.bsuir.task361.publisher.entity.User;
import by.bsuir.task361.publisher.entity.UserRole;
import by.bsuir.task361.publisher.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceSecurityTest {
    @Mock
    private UserRepository userRepository;

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(
                userRepository,
                new PublisherCacheService(new ConcurrentMapCacheManager(CacheNames.USERS)),
                passwordEncoder
        );
    }

    @Test
    void createStoresPasswordInBcryptForm() {
        when(userRepository.existsByLogin("secure-user")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponseTo response = userService.create(new UserRequestTo(
                null,
                "secure-user",
                "password123",
                "Ivan",
                "Ivanov",
                null
        ));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertNotEquals("password123", saved.getPassword());
        assertTrue(saved.getPassword().startsWith("$2"));
        assertTrue(passwordEncoder.matches("password123", saved.getPassword()));
        assertEquals(UserRole.CUSTOMER, saved.getRole());
        assertEquals("secure-user", response.login());
    }
}
