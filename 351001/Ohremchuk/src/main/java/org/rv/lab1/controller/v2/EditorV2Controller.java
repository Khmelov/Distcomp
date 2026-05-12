package org.rv.lab1.controller.v2;

import jakarta.validation.Valid;
import org.rv.lab1.api.ApiPaths;
import org.rv.lab1.dto.EditorRegisterRequestTo;
import org.rv.lab1.dto.EditorRequestTo;
import org.rv.lab1.dto.EditorResponseTo;
import org.rv.lab1.security.V2AccessControl;
import org.rv.lab1.service.EditorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.API_V2 + "/editors")
public class EditorV2Controller {

    private final EditorService editorService;
    private final V2AccessControl access;

    public EditorV2Controller(EditorService editorService, V2AccessControl access) {
        this.editorService = editorService;
        this.access = access;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EditorResponseTo register(@Valid @RequestBody EditorRegisterRequestTo request) {
        return editorService.register(request);
    }

    @GetMapping
    public List<EditorResponseTo> getAll() {
        access.requireUser();
        return editorService.getAll();
    }

    @GetMapping("/{id}")
    public EditorResponseTo getById(@PathVariable long id) {
        access.requireUser();
        return editorService.getById(id);
    }

    @PutMapping("/{id}")
    public EditorResponseTo update(
            @PathVariable long id,
            @Valid @RequestBody EditorRequestTo request
    ) {
        access.requireEditorWrite(id);
        return editorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        access.requireEditorWrite(id);
        editorService.delete(id);
    }
}
