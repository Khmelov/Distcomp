package com.common;

import java.util.List;

public record NoticeAsyncResponse(
        List<com.common.NoticeResponseToDto> noticeMessages,
        String correlationId
) {
}
