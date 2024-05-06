package by.bsuir.dc.lab5.services.interfaces;

import by.bsuir.dc.lab5.entities.Editor;

import java.util.List;

public interface EditorService {
    Editor add(Editor editor);

    void delete(long id);

    Editor update(Editor editor);

    Editor getById(long id);

    List<Editor> getAll();

    Editor getByLogin(String login);
}
