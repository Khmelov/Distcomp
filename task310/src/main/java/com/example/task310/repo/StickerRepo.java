package com.example.task310.repo;

import com.example.task310.domain.Sticker;
import org.springframework.stereotype.Repository;

@Repository
public class StickerRepo extends InMemoryRepo<Sticker> {
    @Override
    protected Sticker withId(Sticker s, long id) {
        return new Sticker(id, s.name());
    }
}
