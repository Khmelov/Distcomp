package by.kopvzakone.distcomp.controllers;

import by.kopvzakone.distcomp.dto.TweetRequestTo;
import by.kopvzakone.distcomp.dto.TweetResponseTo;
import by.kopvzakone.distcomp.services.TweetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1.0/tweets")
@AllArgsConstructor
public class TweetController {
    private final TweetService serviceImpl;
    @GetMapping
    public Collection<TweetResponseTo> getAll(){
        return serviceImpl.getAll();
    }
    @GetMapping("/{id}")
    public TweetResponseTo getById(@PathVariable Long id){
        return serviceImpl.getById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseTo create(@RequestBody @Valid TweetRequestTo request){
        return serviceImpl.create(request);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        serviceImpl.delete(id);
    }
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public TweetResponseTo update(@RequestBody @Valid TweetRequestTo request){
        return serviceImpl.update(request);
    }
}

