package by.bsuir.romamuhtasarov.impl.repository;

import by.bsuir.romamuhtasarov.api.InMemoryRepository;
import by.bsuir.romamuhtasarov.impl.bean.Tag;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TagRepository implements InMemoryRepository<Tag> {
    private final Map<Long, Tag> TagMemory = new HashMap<>();

    @Override
    public Tag get(long id) {
        Tag Tag = TagMemory.get(id);
        if (Tag != null) {
            Tag.setId(id);
        }
        return Tag;
    }

    @Override
    public List<Tag> getAll() {
        List<Tag> TagList = new ArrayList<>();
        for (Long key : TagMemory.keySet()) {
            Tag Tag = TagMemory.get(key);
            Tag.setId(key);
            TagList.add(Tag);
        }
        return TagList;
    }

    @Override
    public Tag delete(long id) {

        return TagMemory.remove(id);
    }

    @Override
    public Tag insert(Tag insertObject) {
        TagMemory.put(insertObject.getId(), insertObject);
        return insertObject;
    }

    @Override
    public boolean update(Tag updatingValue) {
        return TagMemory.replace(updatingValue.getId(), TagMemory.get(updatingValue.getId()), updatingValue);
    }


}