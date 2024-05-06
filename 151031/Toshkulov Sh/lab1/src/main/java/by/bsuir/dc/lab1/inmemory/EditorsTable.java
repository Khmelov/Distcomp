package by.bsuir.dc.lab1.inmemory;

import by.bsuir.dc.lab1.entities.Editor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class EditorsTable {

    private static EditorsTable instance;
    private BigInteger id = BigInteger.valueOf(1);
    private List<Editor> editors = new ArrayList<>();

    private EditorsTable(){

    }

    public Editor add(Editor editor){
        editor.setId(id);
        editors.add(editor);
        id = id.add(BigInteger.valueOf(1));
        return editor;
    }
    public boolean delete(BigInteger id){
        for(Editor editor: editors){
            if(editor.getId().equals(id)){
                return editors.remove(editor);
            }
        }
        return false;
    }
    public Editor getById(BigInteger id){
        for(Editor editor: editors){
            if(editor.getId().equals(id)){
                return editor;
            }
        }
        return null;
    }
    public List<Editor> getAll(){
        return editors;
    }

    public Editor update(Editor editor){
        Editor oldEditor = getById(editor.getId());
        if(oldEditor != null){
            editors.set(editors.indexOf(oldEditor), editor);
            return editor;
        } else {
            return null;
        }
    }

    public static EditorsTable getInstance(){
        if(instance == null){
            instance = new EditorsTable();
        }
        return instance;
    }
}
