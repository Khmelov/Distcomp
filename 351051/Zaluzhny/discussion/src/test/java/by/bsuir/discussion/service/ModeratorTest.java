package by.bsuir.discussion.service;

import by.bsuir.discussion.dto.MessageState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModeratorTest {

    private final Moderator moderator = new Moderator();

    @Test
    void approvesOrdinaryText() {
        assertThat(moderator.decide("hello story")).isEqualTo(MessageState.APPROVE);
    }

    @Test
    void declinesStopWordIgnoringCase() {
        assertThat(moderator.decide("Spam link")).isEqualTo(MessageState.DECLINE);
        assertThat(moderator.decide("full of HATE")).isEqualTo(MessageState.DECLINE);
        assertThat(moderator.decide("abuse")).isEqualTo(MessageState.DECLINE);
    }
}
