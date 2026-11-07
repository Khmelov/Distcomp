package com.distcomp.common.notice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NoticeStateTest {

    @Test
    void acceptsCorrectSpelling() {
        assertThat(NoticeState.fromValue("DECLINE")).isEqualTo(NoticeState.DECLINE);
    }

    @Test
    void acceptsTypoFromAssignment() {
        assertThat(NoticeState.fromValue("DELCINE")).isEqualTo(NoticeState.DECLINE);
    }

    @Test
    void isCaseInsensitive() {
        assertThat(NoticeState.fromValue("approve")).isEqualTo(NoticeState.APPROVE);
    }
}
