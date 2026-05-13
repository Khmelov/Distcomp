package by.boukhvalova.distcomp.controllers;


import by.boukhvalova.distcomp.dto.NoteRequestTo;
import by.boukhvalova.distcomp.dto.NoteResponseTo;
import by.boukhvalova.distcomp.services.NoteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class NoteKafkaController {
    private final NoteService serviceImpl;

    public List<NoteResponseTo> getAll(){
        return serviceImpl.getAll();
    }

    public NoteResponseTo getById(Long id){
        return serviceImpl.getById(id);
    }

    public NoteResponseTo create(NoteRequestTo request){
        return serviceImpl.create(request);
    }

    public void delete(Long id){
        serviceImpl.delete(id);
    }

    public NoteResponseTo update(NoteRequestTo request){
        return serviceImpl.update(request);
    }
}
