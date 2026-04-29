package by.bsuir.task320.service;

import by.bsuir.task320.dto.request.UserRequestTo;
import by.bsuir.task320.exception.ConflictException;
import by.bsuir.task320.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createShouldThrowConflictWhenLoginExists() {
        UserRequestTo request = new UserRequestTo(null, "user1", "password1", "Anton", "Test");
        when(userRepository.existsByLogin("user1")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(request));
    }
}
