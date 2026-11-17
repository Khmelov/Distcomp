package by.bsuir.discussion.controller;

import by.bsuir.discussion.dto.MessageResponseTo;
import by.bsuir.discussion.exception.ApiException;
import by.bsuir.discussion.exception.GlobalExceptionHandler;
import by.bsuir.discussion.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("GET /api/v1.0/messages отвечает 200")
    void listsWithApiPrefix() throws Exception {
        when(messageService.getAll()).thenReturn(List.of(new MessageResponseTo(4L, 2L, "hello")));

        mvc.perform(get("/api/v1.0/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4));
    }

    @Test
    @DisplayName("POST /messages без префикса /api/v1.0 отвечает 201")
    void createsMessage() throws Exception {
        when(messageService.create(any())).thenReturn(new MessageResponseTo(4L, 2L, "hello"));

        mvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"storyId\":2,\"content\":\"hello\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.storyId").value(2))
                .andExpect(jsonPath("$.content").value("hello"));
    }

    @Test
    @DisplayName("Список по storyId читается параметром запроса")
    void listsByStory() throws Exception {
        when(messageService.findByStoryId(2L)).thenReturn(List.of(new MessageResponseTo(4L, 2L, "hello")));

        mvc.perform(get("/messages").param("storyId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4));
    }

    @Test
    @DisplayName("Отсутствующее сообщение возвращает 40401")
    void missingMessage() throws Exception {
        when(messageService.getById(9L)).thenThrow(ApiException.notFound(9L));

        mvc.perform(get("/messages/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(40401));
    }

    @Test
    @DisplayName("Битое JSON даёт 40006")
    void malformedJson() throws Exception {
        mvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(40006));
    }

    @Test
    @DisplayName("Удаление отвечает 204")
    void deletesMessage() throws Exception {
        mvc.perform(delete("/messages/4"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Полный список и обновление идут без префикса версии")
    void listsAndUpdates() throws Exception {
        when(messageService.getAll()).thenReturn(List.of(new MessageResponseTo(1L, 2L, "a")));
        when(messageService.update(org.mockito.ArgumentMatchers.eq(1L), any()))
                .thenReturn(new MessageResponseTo(1L, 3L, "b"));

        mvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("a"));

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/messages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"storyId\":3,\"content\":\"bb\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storyId").value(3));
    }

    @Test
    @DisplayName("Неверный метод и тип тела получают коды 40501 и 41501")
    void rejectsMethodAndMediaType() throws Exception {
        mvc.perform(post("/messages/4"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.errorCode").value(40501));

        mvc.perform(post("/messages")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("hello"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.errorCode").value(41501));
    }

    @Test
    @DisplayName("Неожиданная ошибка сервиса становится 50000")
    void unexpectedError() throws Exception {
        when(messageService.getById(1L)).thenThrow(new IllegalStateException("boom"));

        mvc.perform(get("/messages/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value(50000));
    }
}
