package com.publick.service;

import com.publick.dto.StickerRequestTo;
import com.publick.dto.StickerResponseTo;
import com.publick.entity.Sticker;
import com.publick.repository.StickerRepository;
import com.publick.service.mapper.StickerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StickerServiceTest {

    @Mock
    private StickerRepository stickerRepository;

    @Mock
    private StickerMapper stickerMapper;

    @InjectMocks
    private StickerService stickerService;

    private Sticker sticker;
    private StickerRequestTo request;
    private StickerResponseTo response;

    @BeforeEach
    void setUp() {
        sticker = new Sticker("Test Sticker");
        sticker.setId(1L);

        request = new StickerRequestTo();
        request.setName("Test Sticker");

        response = new StickerResponseTo();
        response.setId(1L);
        response.setName("Test Sticker");
    }

    @Test
    void create_ShouldReturnCreatedSticker() {
        // Given
        when(stickerMapper.toEntity(request)).thenReturn(sticker);
        when(stickerRepository.save(sticker)).thenReturn(sticker);
        when(stickerMapper.toResponse(sticker)).thenReturn(response);

        // When
        StickerResponseTo result = stickerService.create(request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Sticker", result.getName());
        verify(stickerRepository).save(sticker);
        verify(stickerMapper).toResponse(sticker);
    }

    @Test
    void getById_ShouldReturnSticker_WhenExists() {
        // Given
        when(stickerRepository.findById(1L)).thenReturn(Optional.of(sticker));
        when(stickerMapper.toResponse(sticker)).thenReturn(response);

        // When
        StickerResponseTo result = stickerService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(stickerRepository).findById(1L);
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        // Given
        when(stickerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> stickerService.getById(1L));
        assertTrue(exception.getMessage().contains("Sticker not found"));
    }

    @Test
    void getAll_ShouldReturnAllStickers() {
        // Given
        List<Sticker> stickers = Arrays.asList(sticker);
        when(stickerRepository.findAll()).thenReturn(stickers);
        when(stickerMapper.toResponse(sticker)).thenReturn(response);

        // When
        List<StickerResponseTo> result = stickerService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(stickerRepository).findAll();
    }

    @Test
    void getAllPaged_ShouldReturnPagedStickers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Sticker> stickers = Arrays.asList(sticker);
        Page<Sticker> stickerPage = new PageImpl<>(stickers, pageable, 1);
        Page<StickerResponseTo> responsePage = new PageImpl<>(Arrays.asList(response), pageable, 1);

        when(stickerRepository.findAll(pageable)).thenReturn(stickerPage);
        when(stickerMapper.toResponse(sticker)).thenReturn(response);

        // When
        Page<StickerResponseTo> result = stickerService.getAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(stickerRepository).findAll(pageable);
    }

    @Test
    void update_ShouldReturnUpdatedSticker() {
        // Given
        Sticker existingSticker = new Sticker("Old Sticker");
        existingSticker.setId(1L);

        when(stickerRepository.findById(1L)).thenReturn(Optional.of(existingSticker));
        when(stickerRepository.save(any(Sticker.class))).thenReturn(existingSticker);
        when(stickerMapper.toResponse(existingSticker)).thenReturn(response);

        // When
        StickerResponseTo result = stickerService.update(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(stickerRepository).save(existingSticker);
    }

    @Test
    void delete_ShouldDeleteSticker_WhenExists() {
        // Given
        when(stickerRepository.existsById(1L)).thenReturn(true);

        // When
        stickerService.delete(1L);

        // Then
        verify(stickerRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // Given
        when(stickerRepository.existsById(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> stickerService.delete(1L));
        assertTrue(exception.getMessage().contains("Sticker not found"));
    }
}