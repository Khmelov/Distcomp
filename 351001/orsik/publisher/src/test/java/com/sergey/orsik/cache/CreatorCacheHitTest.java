package com.sergey.orsik.cache;

import com.sergey.orsik.config.RedisCacheConfig;
import com.sergey.orsik.dto.request.CreatorRequestTo;
import com.sergey.orsik.dto.response.CreatorResponseTo;
import com.sergey.orsik.entity.Creator;
import com.sergey.orsik.entity.CreatorRole;
import com.sergey.orsik.mapper.CreatorMapper;
import com.sergey.orsik.repository.CreatorRepository;
import com.sergey.orsik.service.CreatorService;
import com.sergey.orsik.service.impl.CreatorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CreatorServiceImpl.class)
@Import({CreatorMapper.class, RedisCacheConfig.class, CreatorCacheHitTest.MockRepoConfig.class})
@ImportAutoConfiguration(CacheAutoConfiguration.class)
class CreatorCacheHitTest {

    @Configuration
    static class MockRepoConfig {

        @Bean
        CreatorRepository creatorRepository() {
            return Mockito.mock(CreatorRepository.class);
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
            when(encoder.encode(any())).thenAnswer(inv -> inv.getArgument(0));
            return encoder;
        }
    }

    @Autowired
    private CreatorRepository creatorRepository;

    @Autowired
    private CreatorService service;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void resetMockAndCaches() {
        Mockito.reset(creatorRepository);
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    @Test
    void findByIdSecondCallUsesCache() {
        Creator entity = new Creator(1L, "u@x.com", "p", "John", "Doe", CreatorRole.CUSTOMER);
        when(creatorRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertEquals(1L, service.findById(1L).getId());
        assertEquals(1L, service.findById(1L).getId());

        verify(creatorRepository, times(1)).findById(1L);
    }

    @Test
    void updateEvictsByIdCache() {
        Creator entity = new Creator(1L, "u@x.com", "p", "John", "Doe", CreatorRole.CUSTOMER);
        Creator updated = new Creator(1L, "u@x.com", "p2", "Jane", "Doe", CreatorRole.CUSTOMER);
        when(creatorRepository.findById(1L))
                .thenReturn(Optional.of(entity))
                .thenReturn(Optional.of(updated));
        when(creatorRepository.existsById(1L)).thenReturn(true);
        when(creatorRepository.existsByLoginAndIdNot("u@x.com", 1L)).thenReturn(false);
        when(creatorRepository.save(any(Creator.class))).thenReturn(updated);

        service.findById(1L);

        CreatorRequestTo req = new CreatorRequestTo(1L, "u@x.com", "p2", "Jane", "Doe", CreatorRole.CUSTOMER);
        service.update(1L, req);

        CreatorResponseTo third = service.findById(1L);
        assertEquals("Jane", third.getFirstname());

        verify(creatorRepository, times(2)).findById(1L);
    }
}
