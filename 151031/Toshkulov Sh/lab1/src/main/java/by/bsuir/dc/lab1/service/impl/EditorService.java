package by.bsuir.dc.lab1.service.impl;

import by.bsuir.dc.lab1.dto.EditorRequestTo;
import by.bsuir.dc.lab1.dto.EditorResponseTo;
import by.bsuir.dc.lab1.dto.mappers.EditorMapper;
import by.bsuir.dc.lab1.entities.Editor;
import by.bsuir.dc.lab1.inmemory.EditorsTable;
import by.bsuir.dc.lab1.service.abst.IEditorService;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class EditorService implements IEditorService {

    @Override
    public EditorResponseTo create(EditorRequestTo editorTo) {
        Editor editor = EditorMapper.instance.convertFromDTO(editorTo);
        editor = EditorsTable.getInstance().add(editor);
        if(editor != null){
            return EditorMapper.instance.convertToDTO(editor);
        } else {
            return null;
        }
    }

    @Override
    public EditorResponseTo getById(BigInteger id) {
        Editor editor = EditorsTable.getInstance().getById(id);
        if(editor != null){
            return EditorMapper.instance.convertToDTO(editor);
        } else {
            return null;
        }
    }

    @Override
    public List<EditorResponseTo> getAll() {
        List<Editor> editors = EditorsTable.getInstance().getAll();
        List<EditorResponseTo> authorsTo = new ArrayList<>();
        for(Editor editor : editors){
            authorsTo.add(EditorMapper.instance.convertToDTO(editor));
        }
        return authorsTo;
    }

    @Override
    public EditorResponseTo update(EditorRequestTo editorTo) {
        Editor updatedEditor = EditorMapper.instance.convertFromDTO(editorTo);
        Editor editor = EditorsTable.getInstance().update(updatedEditor);
        if(editor != null){
            return EditorMapper.instance.convertToDTO(editor);
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(BigInteger id) {
        return EditorsTable.getInstance().delete(id);
    }
}
