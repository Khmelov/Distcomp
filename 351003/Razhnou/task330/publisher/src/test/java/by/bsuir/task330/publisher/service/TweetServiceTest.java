package by.bsuir.task330.publisher.service;

import by.bsuir.task330.publisher.dto.request.TweetRequestTo;
import by.bsuir.task330.publisher.entity.User;
import by.bsuir.task330.publisher.exception.BadRequestException;
import by.bsuir.task330.publisher.repository.TweetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TweetServiceTest {

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserService userService;

    @Mock
    private TagService tagService;

    @InjectMocks
    private TweetService tweetService;

    @Test
    void createShouldThrowBadRequestWhenUserIdIsInvalid() {
        TweetRequestTo request = new TweetRequestTo(null, 0L, "title", "content", null, null, List.of(), List.of());
        assertThrows(BadRequestException.class, () -> tweetService.create(request));
    }

    @Test
    void updateShouldThrowNotFoundWhenTweetMissing() {
        TweetRequestTo request = new TweetRequestTo(1L, 1L, "title", "content", null, null, List.of(), List.of());
        when(userService.getUser(1L)).thenReturn(new User(1L, "user1", "password1", "Anton", "Test"));
        assertThrows(by.bsuir.task330.publisher.exception.NotFoundException.class, () -> tweetService.update(request));
    }
}
