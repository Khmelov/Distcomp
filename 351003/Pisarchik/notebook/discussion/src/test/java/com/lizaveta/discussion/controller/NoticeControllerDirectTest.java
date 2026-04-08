package com.lizaveta.discussion.controller;

import com.lizaveta.discussion.service.NoticeService;
import com.lizaveta.notebook.model.dto.request.NoticeRequestTo;
import com.lizaveta.notebook.model.dto.response.NoticeResponseTo;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoticeControllerDirectTest {

    @Mock
    private NoticeService noticeService;

    @Test
    void create_ShouldDelegate() {
        NoticeController controller = new NoticeController(noticeService);
        NoticeRequestTo req = new NoticeRequestTo(3L, "ab");
        when(noticeService.create(req)).thenReturn(new NoticeResponseTo(9L, 3L, "ab"));
        NoticeResponseTo out = controller.create(req);
        assertThat(out.id()).isEqualTo(9L);
        verify(noticeService).create(req);
    }

    @Test
    void findAll_ShouldReturnListWhenDefaultParams() {
        NoticeController controller = new NoticeController(noticeService);
        when(noticeService.findAll()).thenReturn(List.of(new NoticeResponseTo(1L, 1L, "a")));
        Object result = controller.findAll(0, 20, null, "asc", null);
        assertThat(result).isInstanceOf(List.class);
    }

    @Test
    void findAll_ShouldReturnPageWhenPaged() {
        NoticeController controller = new NoticeController(noticeService);
        when(noticeService.findAll(1, 5, "id", "desc"))
                .thenReturn(new PageResponseTo<>(List.of(), 0, 0, 5, 1));
        Object result = controller.findAll(1, 5, "id", "desc", null);
        assertThat(result).isInstanceOf(PageResponseTo.class);
    }

    @Test
    void findByStory_ShouldDelegate() {
        NoticeController controller = new NoticeController(noticeService);
        when(noticeService.findByStoryId(8L)).thenReturn(List.of());
        controller.findByStoryId(8L);
        verify(noticeService).findByStoryId(8L);
    }
}
