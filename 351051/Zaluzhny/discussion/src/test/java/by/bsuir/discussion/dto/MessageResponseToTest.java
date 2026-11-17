package by.bsuir.discussion.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageResponseToTest {

    @Test
    void keepsAssignedFields() {
        MessageResponseTo response = new MessageResponseTo();
        response.setId(4L);
        response.setStoryId(8L);
        response.setContent("ok");

        assertThat(response.getId()).isEqualTo(4L);
        assertThat(response.getStoryId()).isEqualTo(8L);
        assertThat(response.getContent()).isEqualTo("ok");
    }
}
