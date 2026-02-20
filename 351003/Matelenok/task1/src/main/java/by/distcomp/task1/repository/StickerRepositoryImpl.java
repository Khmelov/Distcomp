package by.distcomp.task1.repository;

import by.distcomp.task1.model.Sticker;
import org.springframework.stereotype.Repository;

@Repository
public class StickerRepositoryImpl extends CrudRepositoryImpl<Sticker>
        implements StickerRepository {

    @Override
    protected Long getId(Sticker sticker) {
        return sticker.getId();
    }

    @Override
    protected void setId(Sticker sticker, Long id) {
        sticker.setId(id);
    }
}