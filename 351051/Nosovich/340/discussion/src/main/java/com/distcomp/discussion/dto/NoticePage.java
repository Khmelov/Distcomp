package com.distcomp.discussion.dto;

import com.distcomp.common.notice.NoticeDto;

import java.util.List;

/**
 * One page of notices together with the total number of matches.
 *
 * @param content notices of the page
 * @param total   total number of notices matching the query
 */
public record NoticePage(List<NoticeDto> content, long total) {
}
