package by.egorsosnovski.distcomp.controllers;

import by.egorsosnovski.distcomp.dto.NoteRequestTo;
import by.egorsosnovski.distcomp.dto.NoteResponseTo;
import by.egorsosnovski.distcomp.services.NoteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("api/v1.0/notes")
@AllArgsConstructor
public class NoteController {
    private final NoteService serviceImpl;
    @GetMapping
    public Collection<NoteResponseTo> getAll(){
        return serviceImpl.getAll();
    }
    @GetMapping("/{id}")
    public NoteResponseTo getById(@PathVariable Long id){
        return serviceImpl.getById(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteResponseTo create(@RequestBody @Valid NoteRequestTo request){
        return serviceImpl.create(request);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        serviceImpl.delete(id);
    }
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public NoteResponseTo update(@RequestBody @Valid NoteRequestTo request){
        return serviceImpl.update(request);
    }
}

