package by.kopvzakone.distcomp.services;

import by.kopvzakone.distcomp.dto.*;
import by.kopvzakone.distcomp.entities.Reaction;
import by.kopvzakone.distcomp.entities.ReactionKey;
import by.kopvzakone.distcomp.repositories.ReactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@AllArgsConstructor
public class ReactionService {
    public final ReactionRepository repImpl;
    @Qualifier("reactionMapper")
    public final ReactionMapper mapper;
    private final WebClient webClient;

    public List<ReactionResponseTo> getAll() {
        return repImpl.getAll().map(mapper::out).toList();
    }
    public ReactionResponseTo getById(Long id) {
        ReactionKey key = new ReactionKey();
        key.setId(id);
        key.setCountry("by");
        return repImpl.get(key).map(mapper::out).orElseThrow();
    }
    public ReactionResponseTo create(ReactionRequestTo req) {
        Reaction reaction = mapper.in(req);
        ensureTweetExistence(reaction);
        return repImpl.create(reaction).map(mapper::out).orElseThrow();
    }
    private void ensureTweetExistence(Reaction reaction){
        try {
            webClient.get()
                    .uri("/api/v1.0/tweets/{id}", reaction.getTweetId())
                    .retrieve()
                    .bodyToMono(ReactionResponseTo.class).hasElement().block();
        }catch (Exception e){
            throw new DataIntegrityViolationException("tweet with id " + reaction.getTweetId() + " doesnt exists");
        }
    }
    public ReactionResponseTo update(ReactionRequestTo req) {
        Reaction reaction = mapper.in(req);
        ensureTweetExistence(reaction);
        return repImpl.update(reaction).map(mapper::out).orElseThrow();
    }
    public void delete(Long id) {
        ReactionKey key = new ReactionKey();
        key.setId(id);
        key.setCountry("by");
        repImpl.delete(key);
    }
}

