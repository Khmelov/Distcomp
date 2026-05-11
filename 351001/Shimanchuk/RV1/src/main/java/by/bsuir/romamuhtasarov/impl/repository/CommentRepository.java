package by.bsuir.romamuhtasarov.impl.repository;

import by.bsuir.romamuhtasarov.api.InMemoryRepository;
import by.bsuir.romamuhtasarov.impl.bean.Comment;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommentRepository implements InMemoryRepository<Comment> {
    private final Map<Long, Comment> CommentMemory = new HashMap<>();

    @Override
    public Comment get(long id) {
        Comment Comment = CommentMemory.get(id);
        if (Comment != null) {
            Comment.setId(id);
        }
        return Comment;
    }

    @Override
    public List<Comment> getAll() {
        List<Comment> CommentList = new ArrayList<>();
        for (Long key : CommentMemory.keySet()) {
            Comment Comment = CommentMemory.get(key);
            Comment.setId(key);
            CommentList.add(Comment);
        }
        return CommentList;
    }

    @Override
    public Comment delete(long id) {

        return CommentMemory.remove(id);
    }

    @Override
    public Comment insert(Comment insertObject) {
        CommentMemory.put(insertObject.getId(), insertObject);
        return insertObject;
    }

    @Override
    public boolean update(Comment updatingValue) {
        return CommentMemory.replace(updatingValue.getId(), CommentMemory.get(updatingValue.getId()), updatingValue);
    }


}