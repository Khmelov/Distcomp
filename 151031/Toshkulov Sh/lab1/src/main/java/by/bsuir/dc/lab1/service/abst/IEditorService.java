package by.bsuir.dc.lab1.service.abst;

import by.bsuir.dc.lab1.dto.EditorRequestTo;
import by.bsuir.dc.lab1.dto.EditorResponseTo;

import java.math.BigInteger;
import java.util.List;

public interface IEditorService {
    EditorResponseTo create(EditorRequestTo editorTo);
    EditorResponseTo getById(BigInteger id);
    List<EditorResponseTo> getAll();
    EditorResponseTo update(EditorRequestTo editorTo);
    boolean delete(BigInteger id);
}
