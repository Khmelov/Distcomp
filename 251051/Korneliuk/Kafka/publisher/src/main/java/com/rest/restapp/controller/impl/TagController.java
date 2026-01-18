package com.rest.restapp.controller.impl;

import com.rest.restapp.controller.TagControllerApi;
import com.rest.restapp.dto.request.TagRequestTo;
import com.rest.restapp.dto.response.TagResponseTo;
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
    public ResponseEntity<TagResponseTo> createTag(TagRequestTo requestTo) {
        var response = tagService.createTag(requestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<TagResponseTo> getTagById(Long id) {
        var response = tagService.getTagById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<TagResponseTo>> getAllTags() {
        var responses = tagService.getAllTags();
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<TagResponseTo> updateTag(Long id, TagRequestTo requestTo) {
        var response = tagService.updateTag(id, requestTo);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteTag(Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}