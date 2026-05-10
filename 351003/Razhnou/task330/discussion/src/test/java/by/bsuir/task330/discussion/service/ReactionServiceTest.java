package by.bsuir.task330.discussion.service;

import by.bsuir.task330.discussion.dto.request.ReactionRequestTo;
import by.bsuir.task330.discussion.exception.BadRequestException;
import by.bsuir.task330.discussion.repository.ReactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private ReactionService reactionService;

    @Test
    void createShouldThrowBadRequestWhenTweetIdIsInvalid() {
        ReactionRequestTo request = new ReactionRequestTo(null, 0L, "reaction");
        assertThrows(BadRequestException.class, () -> reactionService.create(request));
    }
}
