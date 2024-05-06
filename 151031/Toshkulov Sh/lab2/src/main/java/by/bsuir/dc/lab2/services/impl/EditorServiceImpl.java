package by.bsuir.dc.lab2.services.impl;

import by.bsuir.dc.lab2.entities.Editor;
import by.bsuir.dc.lab2.services.interfaces.EditorService;
import by.bsuir.dc.lab2.services.repos.EditorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EditorServiceImpl implements EditorService {

    @Autowired
    private EditorRepository repos;
    @Override
    public Editor add(Editor editor) {
        return repos.save(editor);
    }

    @Override
    public void delete(long id) {
        Optional<Editor> editor = repos.findById(id);
        if(editor.isPresent()) {
            repos.delete(editor.get());
        }
    }

    @Override
    public Editor update(Editor editor) {
        return repos.saveAndFlush(editor);
    }

    @Override
    public Editor getById(long id) {
        Optional<Editor> editor = repos.findById(id);
        if(editor.isPresent()) {
            return editor.get();
        } else {
            return null;
        }
    }

    @Override
    public List<Editor> getAll() {
        return repos.findAll();
    }

    @Override
    public Editor getByLogin(String login) {
        return repos.findByLogin(login);
    }
}
