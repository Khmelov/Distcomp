package by.bsuir.dc.lab1.controllers;

import by.bsuir.dc.lab1.dto.EditorRequestTo;
import by.bsuir.dc.lab1.dto.EditorResponseTo;
import by.bsuir.dc.lab1.service.impl.EditorService;
import by.bsuir.dc.lab1.validators.EditorValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1.0/editors")
public class EditorController {
    @Autowired
    private EditorService service;
    @GetMapping(path="/{id}")
    public ResponseEntity<EditorResponseTo> getEditor(@PathVariable BigInteger id){
        EditorResponseTo response = service.getById(id);
        if (response != null) {
            return new ResponseEntity<>(response,HttpStatus.OK);
        } else {
           return new ResponseEntity<>(new EditorResponseTo(),HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping
    public ResponseEntity<List<EditorResponseTo>> getEditors(){
        List<EditorResponseTo> response = service.getAll();
        if (response != null) {
            return new ResponseEntity<>(response,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping
    public ResponseEntity<EditorResponseTo> updateEditor(@RequestBody EditorRequestTo editorTo){
        EditorResponseTo editor = service.getById(editorTo.getId());
        EditorResponseTo response = null;
        if(editor != null && EditorValidator.validate(editorTo) && editor.getId().equals(editorTo.getId())){
            response = service.update(editorTo);
        }
        if (response != null) {
            return new ResponseEntity<>(response,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new EditorResponseTo(),HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping
    public ResponseEntity<EditorResponseTo> addEditor(@RequestBody EditorRequestTo editorTo){
        EditorResponseTo response = null;
        if(EditorValidator.validate(editorTo)) {
            response = service.create(editorTo);
        }
        if(response != null){
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new EditorResponseTo(),HttpStatus.BAD_REQUEST);
        }

    }
    @DeleteMapping(path="/{id}")
    public ResponseEntity<EditorResponseTo> deleteEditor(@PathVariable BigInteger id){
        boolean isDeleted = service.delete(id);
        if(isDeleted){
            return new ResponseEntity<>(new EditorResponseTo(),HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(new EditorResponseTo(),HttpStatus.BAD_REQUEST);
        }
    }
}
