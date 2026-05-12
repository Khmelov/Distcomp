package by.shaminko.distcomp.controllers;

import by.shaminko.distcomp.dto.TweetRequestTo;
import by.shaminko.distcomp.dto.TweetResponseTo;
import by.shaminko.distcomp.services.TweetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1.0/articles")
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
