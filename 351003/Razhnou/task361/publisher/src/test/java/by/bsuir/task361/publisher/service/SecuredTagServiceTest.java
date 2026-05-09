package by.bsuir.task361.publisher.service;

import by.bsuir.task361.publisher.dto.request.TagRequestTo;
import by.bsuir.task361.publisher.dto.response.TagResponseTo;
import by.bsuir.task361.publisher.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecuredTagServiceTest {
    @Mock
    private TagService tagService;
    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private SecuredTagService securedTagService;

    @Test
    void customerCannotCreateTag() {
        when(currentUserService.isAdmin()).thenReturn(false);

        ApiException exception = assertThrows(ApiException.class, () -> securedTagService.create(new TagRequestTo(null, "java")));

        assertEquals(40301, exception.getErrorCode());
        assertEquals("Access denied", exception.getMessage());
    }

    @Test
    void adminCanCreateTag() {
        when(currentUserService.isAdmin()).thenReturn(true);
        when(tagService.create(new TagRequestTo(null, "java"))).thenReturn(new TagResponseTo(1L, "java"));

        TagResponseTo response = securedTagService.create(new TagRequestTo(null, "java"));

        assertEquals(1L, response.id());
        verify(tagService).create(new TagRequestTo(null, "java"));
    }
}
