package by.bsuir.dc.lab4.controllers;

import by.bsuir.dc.lab4.dto.MarkerRequestTo;
import by.bsuir.dc.lab4.dto.mappers.MarkerMapper;
import by.bsuir.dc.lab4.entities.Marker;
import by.bsuir.dc.lab4.services.interfaces.MarkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerController {
    @Autowired
    private MarkerService markerService;

    @GetMapping(path="/{id}")
    public ResponseEntity<Marker> getById(@PathVariable Long id) {
        Marker marker = markerService.getById(id);
        if(marker != null){
            return new ResponseEntity<>(marker, HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>(new Marker(),HttpStatusCode.valueOf(404));
        }
    }
    @GetMapping
    public ResponseEntity<List<Marker>> listAll() {
        List<Marker> marker = markerService.getAll();
        return new ResponseEntity<>(marker, HttpStatusCode.valueOf(200));
    }

    @PostMapping
    public ResponseEntity<Marker> add(@RequestBody MarkerRequestTo markerRequestTo){
        Marker marker = MarkerMapper.instance.convertFromDTO(markerRequestTo);
        Marker addedMarker = markerService.add(marker);
        return new ResponseEntity<>(addedMarker,HttpStatusCode.valueOf(201));
    }

    @PutMapping
    public ResponseEntity<Marker> update(@RequestBody MarkerRequestTo markerRequestTo){
        Marker marker = MarkerMapper.instance.convertFromDTO(markerRequestTo);
        Marker updatedMarker = markerService.update(marker);
        return new ResponseEntity<>(updatedMarker,HttpStatusCode.valueOf(200));
    }
    @DeleteMapping(path="/{id}")
    public ResponseEntity<Marker> delete(@PathVariable Long id){
        Marker marker = markerService.getById(id);
        if(marker != null){
            markerService.delete(id);
            return new ResponseEntity<>(marker,HttpStatusCode.valueOf(204));
        } else {
            return new ResponseEntity<>(new Marker(),HttpStatusCode.valueOf(404));
        }
    }
}
