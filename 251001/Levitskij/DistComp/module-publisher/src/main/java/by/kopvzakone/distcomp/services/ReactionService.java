package by.kopvzakone.distcomp.services;

import by.kopvzakone.distcomp.dto.*;
import by.kopvzakone.distcomp.repositories.ReactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ReactionService {
    public final ReactionRepository repImpl;
    @Qualifier("reactionMapper")
    public final ReactionMapper mapper;

    public List<ReactionResponseTo> getAll() {
        return repImpl.getAll().map(mapper::out).toList();
    }
    public ReactionResponseTo getById(Long id) {
        return repImpl.get(id).map(mapper::out).orElseThrow();
    }
    public ReactionResponseTo create(ReactionRequestTo req) {
        return repImpl.create(mapper.in(req)).map(mapper::out).orElseThrow();
    }
    public ReactionResponseTo update(ReactionRequestTo req) {
        return repImpl.update(mapper.in(req)).map(mapper::out).orElseThrow();
    }
    public void delete(Long id) {
        repImpl.delete(id);
    }
}

