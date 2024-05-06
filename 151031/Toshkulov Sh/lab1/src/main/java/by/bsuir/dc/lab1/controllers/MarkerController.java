package by.bsuir.dc.lab1.controllers;

import by.bsuir.dc.lab1.dto.MarkerRequestTo;
import by.bsuir.dc.lab1.dto.MarkerResponseTo;
import by.bsuir.dc.lab1.service.impl.MarkerService;
import by.bsuir.dc.lab1.validators.MarkerValidator;
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
@RequestMapping("api/v1.0/markers")
public class MarkerController {
    @Autowired
    private MarkerService service;
    @GetMapping(path="/{id}")
    public ResponseEntity<MarkerResponseTo> getMarker(@PathVariable BigInteger id){
        MarkerResponseTo response = service.getById(id);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new MarkerResponseTo(),HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping
    public ResponseEntity<List<MarkerResponseTo>> getMarkers(){
        List<MarkerResponseTo> response = service.getAll();
        if (response != null) {
            return new ResponseEntity<>(response,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping
    public ResponseEntity<MarkerResponseTo> updateMarker(@RequestBody MarkerRequestTo markerTo){
        MarkerResponseTo marker = service.getById(markerTo.getId());
        MarkerResponseTo response = null;
        if(marker != null && MarkerValidator.validate(markerTo)&& marker.getId().equals(markerTo.getId())){
            response = service.update(markerTo);
        }
        if (response != null) {
            return new ResponseEntity<>(response,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new MarkerResponseTo(),HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping
    public ResponseEntity<MarkerResponseTo> addMarker(@RequestBody MarkerRequestTo markerTo){

        MarkerResponseTo response = null;
        if(MarkerValidator.validate(markerTo)) {
            response = service.create(markerTo);
        }
        if(response != null){
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new MarkerResponseTo(),HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping(path="/{id}")
    public ResponseEntity<MarkerResponseTo> deleteMarker(@PathVariable BigInteger id){
        boolean isDeleted = service.delete(id);
        if(isDeleted){
            return new ResponseEntity<>(new MarkerResponseTo(),HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(new MarkerResponseTo(),HttpStatus.BAD_REQUEST);
        }
    }
}
