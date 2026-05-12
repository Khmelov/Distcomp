package org.rv.lab1.controller.v2;

import jakarta.validation.Valid;
import org.rv.lab1.api.ApiPaths;
import org.rv.lab1.dto.MarkerRequestTo;
import org.rv.lab1.dto.MarkerResponseTo;
import org.rv.lab1.security.V2AccessControl;
import org.rv.lab1.service.MarkerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.API_V2 + "/markers")
public class MarkerV2Controller {

    private final MarkerService service;
    private final V2AccessControl access;

    public MarkerV2Controller(MarkerService service, V2AccessControl access) {
        this.service = service;
        this.access = access;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarkerResponseTo create(@Valid @RequestBody MarkerRequestTo request) {
        access.requireMarkerWrite();
        return service.create(request);
    }

    @GetMapping
    public List<MarkerResponseTo> getAll() {
        access.requireUser();
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MarkerResponseTo getById(@PathVariable long id) {
        access.requireUser();
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public MarkerResponseTo update(@PathVariable long id, @Valid @RequestBody MarkerRequestTo request) {
        access.requireMarkerWrite();
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        access.requireMarkerWrite();
        service.delete(id);
    }
}
