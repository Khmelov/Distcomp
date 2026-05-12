package by.egorsosnovski.distcomp.controllers;

import by.egorsosnovski.distcomp.dto.TweetRequestTo;
import by.egorsosnovski.distcomp.dto.TweetResponseTo;
import by.egorsosnovski.distcomp.services.TweetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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

