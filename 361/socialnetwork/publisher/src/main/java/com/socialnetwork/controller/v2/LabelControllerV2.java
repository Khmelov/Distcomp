package com.socialnetwork.controller.v2;

import com.socialnetwork.dto.request.LabelRequestTo;
import com.socialnetwork.dto.response.LabelResponseTo;
import com.socialnetwork.exception.UnauthorizedException;
import com.socialnetwork.security.SecurityUtil;
import com.socialnetwork.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/labels")
public class LabelControllerV2 {

    @Autowired
    private LabelService labelService;

    @GetMapping
    public ResponseEntity<List<LabelResponseTo>> getAllLabels() {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        List<LabelResponseTo> labels = labelService.getAll();
        return ResponseEntity.ok(labels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelResponseTo> getLabelById(@PathVariable Long id) {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        LabelResponseTo label = labelService.getById(id);
        return ResponseEntity.ok(label);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<LabelResponseTo> getLabelByName(@PathVariable String name) {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        LabelResponseTo label = labelService.findByName(name);
        return ResponseEntity.ok(label);
    }

    @PostMapping
    public ResponseEntity<LabelResponseTo> createLabel(@Valid @RequestBody LabelRequestTo request) {
        // Только ADMIN может создавать метки
        if (!SecurityUtil.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can create labels");
        }
        LabelResponseTo createdLabel = labelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLabel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelResponseTo> updateLabel(@PathVariable Long id,
                                                       @Valid @RequestBody LabelRequestTo request) {
        // Только ADMIN может обновлять метки
        if (!SecurityUtil.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can update labels");
        }
        LabelResponseTo updatedLabel = labelService.update(id, request);
        return ResponseEntity.ok(updatedLabel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        // Только ADMIN может удалять метки
        if (!SecurityUtil.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can delete labels");
        }
        labelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/page")
    public ResponseEntity<Page<LabelResponseTo>> getLabelsPage(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        Page<LabelResponseTo> labels = labelService.getAll(pageable);
        return ResponseEntity.ok(labels);
    }
}

