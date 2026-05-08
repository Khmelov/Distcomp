package com.sergey.orsik.controller;

import com.sergey.orsik.dto.request.CreatorRequestTo;
import com.sergey.orsik.dto.response.CreatorResponseTo;
import com.sergey.orsik.entity.Creator;
import com.sergey.orsik.entity.CreatorRole;
import com.sergey.orsik.service.CreatorService;
import com.sergey.orsik.service.SecuredResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2.0/creators")
public class CreatorV2Controller {

    private final CreatorService creatorService;
    private final SecuredResourceService securedResourceService;

    public CreatorV2Controller(CreatorService creatorService, SecuredResourceService securedResourceService) {
        this.creatorService = creatorService;
        this.securedResourceService = securedResourceService;
    }

    @GetMapping
    public List<CreatorResponseTo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        return creatorService.findAll(page, size, sortBy, sortDir, search);
    }

    @GetMapping("/{id}")
    public CreatorResponseTo findById(@PathVariable Long id) {
        return creatorService.findById(id);
    }

    @GetMapping("/me")
    public CreatorResponseTo currentCreator() {
        Creator current = securedResourceService.requireCurrentCreator();
        return creatorService.findById(current.getId());
    }

    @GetMapping("/me/role")
    public Map<String, CreatorRole> currentRole() {
        Creator current = securedResourceService.requireCurrentCreator();
        return Map.of("role", current.getRole());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatorResponseTo create(@Valid @RequestBody CreatorRequestTo request) {
        if (request.getRole() == null) {
            request.setRole(CreatorRole.CUSTOMER);
        }
        return creatorService.create(request);
    }

    @PutMapping
    public CreatorResponseTo updateByBody(@Valid @RequestBody CreatorRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("id is required for update");
        }
        return securedResourceService.updateCreator(request.getId(), request);
    }

    @PutMapping("/{id}")
    public CreatorResponseTo update(@PathVariable Long id, @Valid @RequestBody CreatorRequestTo request) {
        return securedResourceService.updateCreator(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        securedResourceService.deleteCreator(id);
    }
}
