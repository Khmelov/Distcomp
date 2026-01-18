package com.rest.restapp.controller.impl;

import com.rest.restapp.controller.TagControllerApi;
import com.rest.restapp.dto.request.TagRequestToDto;
import com.rest.restapp.dto.response.TagResponseToDto;
import com.rest.restapp.service.TagService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TagController implements TagControllerApi {

    TagService tagService;

    @Override
    public ResponseEntity<TagResponseToDto> createTag(TagRequestToDto requestTo) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tagService
                        .createTag(requestTo)
                );
    }

    @Override
    public ResponseEntity<TagResponseToDto> getTagById(Long id) {
        return ResponseEntity
                .ok(tagService
                        .getTagById(id)
                );
    }

    @Override
    public ResponseEntity<List<TagResponseToDto>> getAllTags() {
        return ResponseEntity
                .ok(tagService
                        .getAllTags()
                );
    }

    @Override
    public ResponseEntity<TagResponseToDto> updateTag(Long id, TagRequestToDto requestTo) {
        return ResponseEntity
                .ok(tagService
                        .updateTag(id, requestTo)
                );
    }

    @Override
    public ResponseEntity<Void> deleteTag(Long id) {
        tagService.deleteTag(id);
        return ResponseEntity
                .noContent()
                .build();
    }
}