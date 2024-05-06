package by.bsuir.dc.lab4.controllers;

import by.bsuir.dc.lab4.dto.EditorRequestTo;
import by.bsuir.dc.lab4.dto.mappers.EditorMapper;
import by.bsuir.dc.lab4.entities.Editor;
import by.bsuir.dc.lab4.services.interfaces.EditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/editors")
public class EditorController {

    @Autowired
    private EditorService editorService;

    @GetMapping(path="/{id}")
    public ResponseEntity<Editor> getById(@PathVariable Long id) {
        Editor editor = editorService.getById(id);
        if(editor != null){
            return new ResponseEntity<>(editor, HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>(new Editor(),HttpStatusCode.valueOf(404));
        }
    }
    @GetMapping
    public ResponseEntity<List<Editor>> listAll() {
        List<Editor> editor = editorService.getAll();
        return new ResponseEntity<>(editor, HttpStatusCode.valueOf(200));
    }

    @PostMapping
    public ResponseEntity<Editor> add(@RequestBody EditorRequestTo editorRequestTo){
        Editor editor = EditorMapper.instance.convertFromDTO(editorRequestTo);
        Editor addedEditor = editorService.add(editor);
        return new ResponseEntity<>(addedEditor,HttpStatusCode.valueOf(201));
    }

    @PutMapping
    public ResponseEntity<Editor> update(@RequestBody EditorRequestTo editorRequestTo){
        Editor editor = EditorMapper.instance.convertFromDTO(editorRequestTo);
        Editor updatedEditor = editorService.update(editor);
        return new ResponseEntity<>(updatedEditor,HttpStatusCode.valueOf(200));
    }
    @DeleteMapping(path="/{id}")
    public ResponseEntity<Editor> delete(@PathVariable Long id){
        Editor editor = editorService.getById(id);
        if(editor != null){
            editorService.delete(id);
            return new ResponseEntity<>(editor,HttpStatusCode.valueOf(204));
        } else {
            return new ResponseEntity<>(new Editor(),HttpStatusCode.valueOf(404));
        }
    }
}
