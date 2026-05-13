package by.bsuir.task330.publisher.service;

import by.bsuir.task330.publisher.domain.Sticker;
import by.bsuir.task330.publisher.dto.request.StickerRequestTo;
import by.bsuir.task330.publisher.dto.response.StickerResponseTo;

import java.util.List;
import java.util.Set;

public interface StickerService {
    StickerResponseTo create(StickerRequestTo request);
    StickerResponseTo update(StickerRequestTo request);
    StickerResponseTo findById(Long id);
    List<StickerResponseTo> findAll(Integer page, Integer size, String sort, String filter);
    void delete(Long id);

    Sticker requireEntity(Long id);
    Set<Sticker> requireEntities(Set<Long> ids);
}
