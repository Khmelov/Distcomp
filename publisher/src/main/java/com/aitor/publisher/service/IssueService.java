package com.aitor.publisher.service;

import com.aitor.publisher.dto.IssueRequestTo;
import com.aitor.publisher.dto.IssueResponseTo;
import com.aitor.publisher.exception.EntityNotExistsException;
import com.aitor.publisher.model.Issue;
import com.aitor.publisher.model.IssueSticker;
import com.aitor.publisher.model.Sticker;
import com.aitor.publisher.model.User;
import com.aitor.publisher.repository.IssueRepository;
import com.aitor.publisher.repository.IssueStickerRepository;
import com.aitor.publisher.repository.StickerRepository;
import com.aitor.publisher.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository repository;
    private final UserRepository userRepository;
    private final StickerRepository stickerRepository;
    private final IssueStickerRepository issueStickerRepository;

    public IssueResponseTo add(IssueRequestTo requestBody){
        var persisted = repository.save(new Issue(
                getUser(requestBody.getUserId()),
                requestBody.getTitle(),
                requestBody.getContent(),
                LocalDateTime.now(),
                LocalDateTime.now()));
        if (requestBody.getStickers() != null)
            for (var stickerName : requestBody.getStickers()){
                var dbStickerList = stickerRepository.findByName(stickerName);
                issueStickerRepository.save(new IssueSticker(persisted,
                        dbStickerList.isEmpty() ?
                                stickerRepository.save(new Sticker(stickerName)) :
                                getSticker(dbStickerList.getFirst().getId())));
            }
        return toResponse(persisted);
    }

    public IssueResponseTo set(Long id, IssueRequestTo requestBody){
        var entity = getEntity(id);
        entity.setUserId(getUser(requestBody.getUserId()));
        entity.setTitle(requestBody.getTitle());
        entity.setContent(requestBody.getContent());
        entity.setModified(LocalDateTime.now());
        return toResponse(repository.save(entity));
    }

    public IssueResponseTo get(Long id) {
        return toResponse(getEntity(id));
    }

    public List<IssueResponseTo> getAll(){
        return repository.findAll().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());
    }

    public IssueResponseTo remove(Long id) {
        var entityOptional = repository.findById(id);
        if (entityOptional.isPresent()) {
            var entity = entityOptional.get();
            var response = toResponse(entity);
            for (var issueSticker : issueStickerRepository.findByIssueId(entity)) {
                issueStickerRepository.delete(issueSticker);
                stickerRepository.delete(issueSticker.getStickerId());
            }
            repository.delete(entity);
            return response;
        } else
            throw new EntityNotExistsException();
    }

    private Issue getEntity(Long id){
        var entity = repository.findById(id);
        if (entity.isPresent())
            return entity.get();
        throw new EntityNotExistsException();
    }

    private User getUser(Long id){
        var entity = userRepository.findById(id);
        if (entity.isPresent())
            return entity.get();
        throw new EntityNotExistsException();
    }

    private Sticker getSticker(Long id){
        var entity = stickerRepository.findById(id);
        if (entity.isPresent())
            return entity.get();
        throw new EntityNotExistsException();
    }

    private IssueResponseTo toResponse(Issue entity){
        return new IssueResponseTo(
                entity.getId(),
                entity.getUserId().getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreated(),
                entity.getModified(),
                null);
    }
}
