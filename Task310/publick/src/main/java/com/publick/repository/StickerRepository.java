package com.publick.repository;

import com.publick.entity.Sticker;
import org.springframework.stereotype.Repository;

@Repository
public class StickerRepository extends InMemoryCrudRepository<Sticker, Long> {

    @Override
    protected Long getId(Sticker entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Sticker entity, Long id) {
        entity.setId(id);
    }
}