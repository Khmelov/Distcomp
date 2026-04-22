package com.sergey.orsik.service;

import com.sergey.orsik.dto.request.CreatorRequestTo;
import com.sergey.orsik.dto.response.CreatorResponseTo;
import com.sergey.orsik.entity.Creator;
import com.sergey.orsik.exception.EntityNotFoundException;
import com.sergey.orsik.mapper.CreatorMapper;
import com.sergey.orsik.repository.CreatorRepository;
import com.sergey.orsik.service.impl.CreatorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatorServiceImplTest {

    @Mock
    private CreatorRepository repository;

    @Mock
    private CreatorMapper mapper;

    private CreatorServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CreatorServiceImpl(repository, mapper);
    }

    @Test
    void findByIdReturnsDtoWhenEntityExists() {
        Creator entity = new Creator(1L, "user@mail.com", "pass", "John", "Doe");
        CreatorResponseTo response = new CreatorResponseTo(1L, "user@mail.com", "John", "Doe");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        CreatorResponseTo result = service.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("user@mail.com", result.getLogin());
    }

    @Test
    void findByIdThrowsWhenEntityMissing() {
        when(repository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.findById(10L));
    }

    @Test
    void createSetsNullIdAndReturnsSavedDto() {
        CreatorRequestTo request = new CreatorRequestTo(null, "new@mail.com", "pass", "New", "User");
        Creator entity = new Creator(null, "new@mail.com", "pass", "New", "User");
        Creator saved = new Creator(100L, "new@mail.com", "pass", "New", "User");
        CreatorResponseTo response = new CreatorResponseTo(100L, "new@mail.com", "New", "User");

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        CreatorResponseTo result = service.create(request);
        assertEquals(100L, result.getId());
        assertEquals("new@mail.com", result.getLogin());
    }
}
