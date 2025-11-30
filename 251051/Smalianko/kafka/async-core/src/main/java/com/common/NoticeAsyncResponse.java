package com.common;

import java.util.List;

public record NoticeAsyncResponse(
        List<NoticeResponseToDto> noticeMessages,
        String correlationId
) {
}
