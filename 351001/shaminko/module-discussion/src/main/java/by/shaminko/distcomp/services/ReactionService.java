package by.shaminko.distcomp.services;

import by.shaminko.distcomp.dto.ReactionMapper;
import by.shaminko.distcomp.dto.ReactionRequestTo;
import by.shaminko.distcomp.dto.ReactionResponseTo;
import by.shaminko.distcomp.dto.*;
import by.shaminko.distcomp.entities.Reaction;
import by.shaminko.distcomp.repositories.ReactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReactionService {
    private static final String APPROVED_STATE = "APPROVE";
    private static final String DECLINED_STATE = "DELCINE";
    private static final List<String> STOP_WORDS = List.of("spam", "scam", "fraud", "hate");

    public final ReactionRepository repImpl;
    @Qualifier("reactionMapper")
    public final ReactionMapper mapper;

    public List<ReactionResponseTo> getAll() {
        return repImpl.getAll().map(mapper::out).toList();
    }

    public ReactionResponseTo getById(Long id) {
        return repImpl.get(id).map(mapper::out)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
    }

    public ReactionResponseTo create(ReactionRequestTo req) {
        Reaction reaction = mapper.in(req);
        reaction.setId(Math.abs(UUID.randomUUID().getMostSignificantBits()));
        reaction.setState(moderate(reaction.getContent()));
        return repImpl.create(reaction).map(mapper::out)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public ReactionResponseTo update(ReactionRequestTo req) {
        if (repImpl.get(req.getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reaction not found");
        }
        Reaction reaction = mapper.in(req);
        reaction.setState(moderate(reaction.getContent()));
        return repImpl.update(reaction).map(mapper::out)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public void delete(Long id) {
        repImpl.delete(id);
    }

    private String moderate(String content) {
        String normalized = content == null ? "" : content.toLowerCase();
        return STOP_WORDS.stream().anyMatch(normalized::contains) ? DECLINED_STATE : APPROVED_STATE;
    }
}