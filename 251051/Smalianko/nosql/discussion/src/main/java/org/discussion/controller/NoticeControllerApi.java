package org.discussion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.discussion.dto.request.NoticeRequestToDto;
import org.discussion.dto.response.NoticeResponseToDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Notices", description = "CRUD operations for Notices")
public interface NoticeControllerApi {

    @Operation(summary = "Create Notice")
    @ApiResponse(responseCode = "201", description = "Notice создан")
    @PostMapping("/notices")
    ResponseEntity<NoticeResponseToDto> createNotice(@Valid @RequestBody NoticeRequestToDto requestTo);

    @Operation(summary = "Get Notice by id")
    @ApiResponse(responseCode = "200", description = "Notice найден")
    @GetMapping("/notices/{id}")
    ResponseEntity<NoticeResponseToDto> getNoticeById(@PathVariable Long id);

    @Operation(summary = "Get all Notices")
    @ApiResponse(responseCode = "200", description = "Все notices")
    @GetMapping("/notices")
    ResponseEntity<List<NoticeResponseToDto>> getAllNotices();

    @Operation(summary = "Update Notice by id")
    @ApiResponse(responseCode = "200", description = "Notice обновлен")
    @PutMapping("/notices/{id}")
    ResponseEntity<NoticeResponseToDto> updateNotice(@PathVariable Long id,
                                                  @Valid @RequestBody NoticeRequestToDto requestTo);

    @Operation(summary = "Delete Notice by id")
    @ApiResponse(responseCode = "204", description = "Notice удалён")
    @DeleteMapping("/notices/{id}")
    ResponseEntity<Void> deleteNotice(@PathVariable Long id);

    @Operation(summary = "Get Notices by Issue id")
    @ApiResponse(responseCode = "200", description = "Notices по issueId")
    @GetMapping("/notices/issue/{issueId}")
    ResponseEntity<List<NoticeResponseToDto>> getNoticesByIssueId(@PathVariable Long issueId);
}