package com.distcomp.discussion.service;

import com.distcomp.common.notice.NoticeState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModerationServiceTest {

    private final ModerationService moderationService = new ModerationService();

    @Test
    void approvesNeutralContent() {
        assertThat(moderationService.moderate("Полезное замечание по задаче"))
                .isEqualTo(NoticeState.APPROVE);
    }

    @Test
    void declinesContentWithStopWord() {
        assertThat(moderationService.moderate("This is spam"))
                .isEqualTo(NoticeState.DECLINE);
    }

    @Test
    void recognisesRussianStopWord() {
        assertThat(moderationService.moderate("Здесь реклама другого курса"))
                .isEqualTo(NoticeState.DECLINE);
    }

    @Test
    void declinesEmptyContent() {
        assertThat(moderationService.moderate("   "))
                .isEqualTo(NoticeState.DECLINE);
    }
}
