package com.aitor.publisher.controller;

import com.aitor.publisher.dto.IssueRequestTo;
import com.aitor.publisher.dto.IssueResponseTo;
import com.aitor.publisher.service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("issues")
public class IssueController {
    private final IssueService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public IssueResponseTo add(@RequestBody @Valid IssueRequestTo request){
        return service.add(request);
    }

    @PutMapping("/{id}")
    public IssueResponseTo set(@PathVariable Long id, @RequestBody @Valid IssueRequestTo request){
        return service.set(id, request);
    }

    @GetMapping("/{id}")
    public IssueResponseTo get(@PathVariable Long id){
        return service.get(id);
    }

    @GetMapping()
    public List<IssueResponseTo> getAll(){
        return service.getAll();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public IssueResponseTo remove(@PathVariable Long id){
        return service.remove(id);
    }
}
