package by.bsuir.discussion.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageKeyTest {

    @Test
    @DisplayName("Ключ сравнивается по storyId и id")
    void equalityUsesBothParts() {
        MessageKey left = new MessageKey(2L, 5L);
        MessageKey same = new MessageKey(2L, 5L);
        MessageKey other = new MessageKey(3L, 5L);

        assertThat(left).isEqualTo(same).hasSameHashCodeAs(same).isNotEqualTo(other).isNotEqualTo(null);
        assertThat(left).isNotEqualTo("2:5");
        left.setStoryId(9L);
        left.setId(1L);
        assertThat(left.getStoryId()).isEqualTo(9L);
        assertThat(left.getId()).isEqualTo(1L);
    }
}
