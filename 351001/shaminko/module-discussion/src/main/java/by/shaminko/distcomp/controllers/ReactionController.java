package by.shaminko.distcomp.controllers;

import by.shaminko.distcomp.dto.ReactionRequestTo;
import by.shaminko.distcomp.dto.ReactionResponseTo;
import by.shaminko.distcomp.services.ReactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping({"/api/v1.0/messages"})
@AllArgsConstructor
public class ReactionController {

    private final ReactionService serviceImpl;

    @GetMapping
    public Collection<ReactionResponseTo> getAll() {
        return serviceImpl.getAll();
    }

    @GetMapping("/{id}")
    public ReactionResponseTo getById(@PathVariable Long id){
        return serviceImpl.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReactionResponseTo create(@RequestBody @Valid ReactionRequestTo request){
        return serviceImpl.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        serviceImpl.delete(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ReactionResponseTo update(@RequestBody @Valid ReactionRequestTo request){
        return serviceImpl.update(request);
    }
}
