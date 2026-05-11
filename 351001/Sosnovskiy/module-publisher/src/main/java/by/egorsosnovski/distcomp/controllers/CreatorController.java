package by.egorsosnovski.distcomp.controllers;

import by.egorsosnovski.distcomp.dto.CreatorRequestTo;
import by.egorsosnovski.distcomp.dto.CreatorResponseTo;
import by.egorsosnovski.distcomp.services.CreatorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("api/v1.0/creators")
@AllArgsConstructor
public class CreatorController {
    private final CreatorService serviceImpl;
    @GetMapping
    public Collection<CreatorResponseTo> getAll(){
        return serviceImpl.getAll();
    }
    @GetMapping("/{id}")
    public CreatorResponseTo getById(@PathVariable Long id){
        return serviceImpl.getById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatorResponseTo create(@RequestBody @Valid CreatorRequestTo request){
        return serviceImpl.create(request);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        serviceImpl.delete(id);
    }
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CreatorResponseTo update(@RequestBody @Valid CreatorRequestTo request){
        return serviceImpl.update(request);
    }
}
