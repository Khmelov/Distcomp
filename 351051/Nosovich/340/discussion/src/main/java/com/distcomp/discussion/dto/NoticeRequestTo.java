package com.distcomp.discussion.dto;

import com.distcomp.common.notice.NoticeState;

/**
 * Incoming representation of a notice used by the discussion REST controller.
 *
 * @param id      identifier, mandatory for update
 * @param issueId identifier of the related issue
 * @param content text of the notice
 * @param state   ignored on input; the moderator decides the state
 */
public record NoticeRequestTo(
        Long id,
        Long issueId,
        String content,
        NoticeState state) {
}
