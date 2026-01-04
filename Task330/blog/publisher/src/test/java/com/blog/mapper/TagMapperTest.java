package com.blog.mapper;

import com.blog.dto.request.TagRequestTo;
import com.blog.dto.response.TagResponseTo;
import com.blog.model.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TagMapperTest {

    @Autowired
    private TagMapper tagMapper;

    @Test
    void shouldMapTagRequestToEntity() {
        // Given
        TagRequestTo request = new TagRequestTo();
        request.setName("Java");

        // When
        Tag entity = tagMapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("Java");
    }

    @Test
    void shouldMapEntityToTagResponse() {
        // Given
        Tag entity = new Tag();
        entity.setId(1L);
        entity.setName("Java");

        // When
        TagResponseTo response = tagMapper.toResponse(entity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Java");
    }
}