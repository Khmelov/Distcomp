package com.sergey.orsik.discussion.service;

import com.sergey.orsik.dto.CommentState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentModerationServiceTest {

    private final CommentModerationService moderation = new CommentModerationService("spam,badword");

    @Test
    void approvesCleanText() {
        assertThat(moderation.moderate("nice comment")).isEqualTo(CommentState.APPROVE);
    }

    @Test
    void declinesStopWord() {
        assertThat(moderation.moderate("this is spam")).isEqualTo(CommentState.DECLINE);
    }

    @Test
    void approvesWhenContainsOrsikMarker() {
        assertThat(moderation.moderate("Фамилия Орсик")).isEqualTo(CommentState.APPROVE);
    }
}
