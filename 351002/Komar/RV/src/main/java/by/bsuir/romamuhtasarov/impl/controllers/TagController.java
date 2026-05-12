package by.bsuir.romamuhtasarov.impl.controllers;

import by.bsuir.romamuhtasarov.impl.service.TagService;
import by.bsuir.romamuhtasarov.impl.dto.TagRequestTo;
import by.bsuir.romamuhtasarov.impl.dto.TagResponseTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
public class TagController {
    @Autowired
    private TagService TagService;

    @GetMapping("/tags")
    public ResponseEntity<List<TagResponseTo>> getAllTags() {
        List<TagResponseTo> TagResponseToList = TagService.getAll();
        return new ResponseEntity<>(TagResponseToList, HttpStatus.OK);
    }

    @GetMapping("/tags/{id}")
    public ResponseEntity<TagResponseTo> getTag(@PathVariable long id) {
        TagResponseTo TagResponseTo = TagService.get(id);
        return new ResponseEntity<>(TagResponseTo, TagResponseTo == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @PostMapping("/tags")
    public ResponseEntity<TagResponseTo> createTag(@RequestBody TagRequestTo TagTo) {
        TagResponseTo addedTag = TagService.add(TagTo);
        return new ResponseEntity<>(addedTag, HttpStatus.CREATED);
    }

    @DeleteMapping("/tags/{id}")
    public ResponseEntity<TagResponseTo> deleteTag(@PathVariable long id) {
        TagResponseTo deletedTag = TagService.delete(id);
        return new ResponseEntity<>(deletedTag, deletedTag == null ? HttpStatus.NOT_FOUND : HttpStatus.NO_CONTENT);
    }

    @PutMapping("/tags")
    public ResponseEntity<TagResponseTo> updateTag(@RequestBody TagRequestTo TagRequestTo) {
        TagResponseTo TagResponseTo = TagService.update(TagRequestTo);
        return new ResponseEntity<>(TagResponseTo, TagResponseTo.getName() == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }
}