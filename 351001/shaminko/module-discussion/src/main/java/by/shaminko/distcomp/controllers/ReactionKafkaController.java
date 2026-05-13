package by.shaminko.distcomp.controllers;


import by.shaminko.distcomp.dto.ReactionRequestTo;
import by.shaminko.distcomp.dto.ReactionResponseTo;
import by.shaminko.distcomp.services.ReactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class ReactionKafkaController {
    private final ReactionService serviceImpl;

    public List<ReactionResponseTo> getAll(){
        return serviceImpl.getAll();
    }

    public ReactionResponseTo getById(Long id){
        return serviceImpl.getById(id);
    }

    public ReactionResponseTo create(ReactionRequestTo request){
        return serviceImpl.create(request);
    }

    public void delete(Long id){
        serviceImpl.delete(id);
    }

    public ReactionResponseTo update(ReactionRequestTo request){
        return serviceImpl.update(request);
    }
}
