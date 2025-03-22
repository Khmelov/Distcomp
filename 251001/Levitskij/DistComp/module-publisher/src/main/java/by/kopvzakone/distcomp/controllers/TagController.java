package by.kopvzakone.distcomp.controllers;

import by.kopvzakone.distcomp.dto.TagRequestTo;
import by.kopvzakone.distcomp.dto.TagResponseTo;
import by.kopvzakone.distcomp.services.TagService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1.0/tags")
@AllArgsConstructor
public class TagController {
    private final TagService serviceImpl;
    @GetMapping
    public Collection<TagResponseTo> getAll(){
        return serviceImpl.getAll();
    }
    @GetMapping("/{id}")
    public TagResponseTo getById(@PathVariable Long id){
        return serviceImpl.getById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponseTo create(@RequestBody @Valid TagRequestTo request){
        return serviceImpl.create(request);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        serviceImpl.delete(id);
    }
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public TagResponseTo update(@RequestBody @Valid TagRequestTo request){
        try{
            return serviceImpl.update(request);
        }catch( NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }
}