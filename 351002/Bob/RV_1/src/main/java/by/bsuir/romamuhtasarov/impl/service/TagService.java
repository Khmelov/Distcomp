package by.bsuir.romamuhtasarov.impl.service;


import by.bsuir.romamuhtasarov.api.Service;
import by.bsuir.romamuhtasarov.api.TagMapper;
import by.bsuir.romamuhtasarov.impl.bean.Tag;
import by.bsuir.romamuhtasarov.impl.dto.*;
import by.bsuir.romamuhtasarov.impl.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TagService implements Service<TagResponseTo, TagRequestTo> {
    @Autowired
    private TagRepository TagRepository;

    public TagService() {

    }

    public List<TagResponseTo> getAll() {
        List<Tag> TagList = TagRepository.getAll();
        List<TagResponseTo> resultList = new ArrayList<>();
        for (int i = 0; i < TagList.size(); i++) {
            resultList.add(TagMapper.INSTANCE.TagToTagResponseTo(TagList.get(i)));
        }
        return resultList;
    }

    public TagResponseTo update(TagRequestTo updatingTag) {
        Tag Tag = TagMapper.INSTANCE.TagRequestToToTag(updatingTag);
        if (validateTag(Tag)) {
            boolean result = TagRepository.update(Tag);
            TagResponseTo responseTo = result ? TagMapper.INSTANCE.TagToTagResponseTo(Tag) : null;
            return responseTo;
        } else return new TagResponseTo();
        //return responseTo;
    }

    public TagResponseTo get(long id) {
        return TagMapper.INSTANCE.TagToTagResponseTo(TagRepository.get(id));
    }

    public TagResponseTo delete(long id) {
        return TagMapper.INSTANCE.TagToTagResponseTo(TagRepository.delete(id));
    }

    public TagResponseTo add(TagRequestTo TagRequestTo) {
        Tag Tag = TagMapper.INSTANCE.TagRequestToToTag(TagRequestTo);
        return TagMapper.INSTANCE.TagToTagResponseTo(TagRepository.insert(Tag));
    }

    private boolean validateTag(Tag Tag) {
        String content = Tag.getName();
        if (content.length() >= 2 && content.length() <= 2048) return true;
        return false;
    }
}