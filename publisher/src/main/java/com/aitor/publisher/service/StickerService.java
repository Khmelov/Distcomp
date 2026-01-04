package com.aitor.publisher.service;

import com.aitor.publisher.dto.StickerRequestTo;
import com.aitor.publisher.dto.StickerResponseTo;
import com.aitor.publisher.exception.EntityNotExistsException;
import com.aitor.publisher.model.Sticker;
import com.aitor.publisher.repository.StickerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StickerService {
    private final StickerRepository repository;

    public StickerResponseTo add(StickerRequestTo requestBody){
        Sticker persisted = repository.save(new Sticker(requestBody.getName()));
        return toResponse(persisted);
    }

    public StickerResponseTo set(Long id, StickerRequestTo requestBody){
        var entity = getEntity(id);
        entity.setName(requestBody.getName());
        return toResponse(repository.save(entity));
    }

    public StickerResponseTo get(Long id) {
        return toResponse(getEntity(id));
    }

    public List<StickerResponseTo> getAll(){
        return repository.findAll().stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());
    }

    public StickerResponseTo remove(Long id) {
        var entityOptional = repository.findById(id);
        if (entityOptional.isPresent()) {
            var entity = entityOptional.get();
            var response = toResponse(entity);
            repository.delete(entity);
            return response;
        } else
            throw new EntityNotExistsException();
    }

    private Sticker getEntity(Long id){
        var entity = repository.findById(id);
        if (entity.isPresent())
            return entity.get();
        throw new EntityNotExistsException();
    }

    private StickerResponseTo toResponse(Sticker entity){
        return new StickerResponseTo(entity.getId(), entity.getName());
    }
}
