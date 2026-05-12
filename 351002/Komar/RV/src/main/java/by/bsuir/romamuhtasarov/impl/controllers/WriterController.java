package by.bsuir.romamuhtasarov.impl.controllers;

import by.bsuir.romamuhtasarov.impl.service.WriterService;
import by.bsuir.romamuhtasarov.impl.dto.WriterResponseTo;
import by.bsuir.romamuhtasarov.impl.dto.WriterRequestTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1.0", consumes = {"application/JSON"}, produces = {"application/JSON"})
public class WriterController {

    @Autowired
    private WriterService writerService;

    @GetMapping("/writers")
    public ResponseEntity<List<WriterResponseTo>> getAllWriters() {
        List<WriterResponseTo> writerResponseToList = writerService.getAll();
        return new ResponseEntity<>(writerResponseToList, HttpStatus.OK);
    }

    @GetMapping("/writers/{id}")
    public ResponseEntity<WriterResponseTo> getWriter(@PathVariable long id) {
        WriterResponseTo writerResponseTo = writerService.get(id);
        return new ResponseEntity<>(writerResponseTo, writerResponseTo == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @PostMapping("/writers")
    public ResponseEntity<WriterResponseTo> createWriter(@RequestBody WriterRequestTo WriterTo) {
        WriterResponseTo addedWriter = writerService.add(WriterTo);
        return new ResponseEntity<>(addedWriter, HttpStatus.CREATED);
    }

    @DeleteMapping("/writers/{id}")
    public ResponseEntity<WriterResponseTo> deleteWriter(@PathVariable long id) {
        WriterResponseTo deletedWriter = writerService.delete(id);
        return new ResponseEntity<>(deletedWriter, deletedWriter == null ? HttpStatus.NOT_FOUND : HttpStatus.NO_CONTENT);
    }

    @PutMapping("/writers")
    public ResponseEntity<WriterResponseTo> updateWriter(@RequestBody WriterRequestTo writerRequestTo) {
        WriterResponseTo writerResponseTo = writerService.update(writerRequestTo);
        return new ResponseEntity<>(writerResponseTo, writerResponseTo.getFirstname() == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

}