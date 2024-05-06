package by.bsuir.dc.lab2.services.interfaces;

import by.bsuir.dc.lab2.entities.Editor;
import by.bsuir.dc.lab2.entities.Marker;

import java.util.List;

public interface MarkerService {

    Marker add(Marker marker);

    void delete(long id);

    Marker update(Marker marker);

    Marker getById(long id);

    List<Marker> getAll();

    Marker getByName(String name);
}
