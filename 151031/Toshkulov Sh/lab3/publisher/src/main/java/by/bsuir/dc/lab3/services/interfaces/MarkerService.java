package by.bsuir.dc.lab3.services.interfaces;

import by.bsuir.dc.lab3.entities.Editor;
import by.bsuir.dc.lab3.entities.Marker;

import java.util.List;

public interface MarkerService {

    Marker add(Marker marker);

    void delete(long id);

    Marker update(Marker marker);

    Marker getById(long id);

    List<Marker> getAll();

    Marker getByName(String name);
}
