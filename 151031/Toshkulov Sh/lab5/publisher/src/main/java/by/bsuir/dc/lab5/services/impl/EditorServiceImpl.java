package by.bsuir.dc.lab5.services.impl;

import by.bsuir.dc.lab5.entities.Editor;
import by.bsuir.dc.lab5.redis.RedisEditorRepository;
import by.bsuir.dc.lab5.services.interfaces.EditorService;
import by.bsuir.dc.lab5.services.repos.EditorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EditorServiceImpl implements EditorService {

    @Autowired
    private EditorRepository repos;

    @Autowired
    private RedisEditorRepository redisRepos;

    @Override
    public Editor add(Editor editor) {
        Editor saved = repos.save(editor);
        redisRepos.add(saved);
        return saved;
    }

    @Override
    public void delete(long id) {
        Optional<Editor> editor = repos.findById(id);
        if(editor.isPresent()) {
            redisRepos.delete(id);
            repos.delete(editor.get());

        }
    }

    @Override
    public Editor update(Editor editor) {
        Optional<Editor> cachedEditor = redisRepos.getById(editor.getId());
        if(cachedEditor.isPresent() && editor.equals(cachedEditor.get())){
            return editor;
        }
        Editor updatedEditor = repos.saveAndFlush(editor);
        redisRepos.update(updatedEditor);
        return updatedEditor;
    }

    @Override
    public Editor getById(long id) {
        Optional<Editor> editor = redisRepos.getById(id);

        if(editor.isPresent()) {
            return editor.get();
        } else {
            editor = repos.findById(id);
            if(editor.isPresent()) {
                redisRepos.add(editor.get());
                return editor.get();
            } else {
                return null;
            }
        }
    }

    @Override
    public List<Editor> getAll() {
        List<Editor> editors = redisRepos.getAll();
        if(!editors.isEmpty()){
            return editors;
        }else{
            editors = repos.findAll();
            if(!editors.isEmpty()){
                for(Editor currentEditor : editors){
                    redisRepos.add(currentEditor);
                }
                return editors;
            }else{
                return new ArrayList<>();
            }
        }
    }

    @Override
    public Editor getByLogin(String login) {
        return repos.findByLogin(login);
    }
}
