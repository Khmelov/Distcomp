package com.rest.restapp.controller.impl;

import com.rest.restapp.controller.IssueControllerApi;
import com.rest.restapp.dto.request.IssueRequestToDto;
import com.rest.restapp.dto.response.UserResponseTo;
import com.rest.restapp.dto.response.IssueResponseToDto;
import com.rest.restapp.service.IssueService;
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
public class IssueController implements IssueControllerApi {

    IssueService issueService;

    @Override
    public ResponseEntity<IssueResponseToDto> createIssue(IssueRequestToDto requestTo) {
        var response = issueService.createIssue(requestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<IssueResponseToDto> getIssueById(Long id) {
        var response = issueService.getIssueById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<IssueResponseToDto>> getAllIssues() {
        var responses = issueService.getAllIssues();
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<IssueResponseToDto> updateIssue(Long id, IssueRequestToDto requestTo) {
        var response = issueService.updateIssue(id, requestTo);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteIssue(Long id) {
        issueService.deleteIssue(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserResponseTo> getUserByIssueId(Long id) {
        var response = issueService.getUserByIssueId(id);
        return ResponseEntity.ok(response);
    }
}