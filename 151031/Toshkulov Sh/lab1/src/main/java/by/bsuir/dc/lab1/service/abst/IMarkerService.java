package by.bsuir.dc.lab1.service.abst;
import by.bsuir.dc.lab1.dto.MarkerRequestTo;
import by.bsuir.dc.lab1.dto.MarkerResponseTo;

import java.math.BigInteger;
import java.util.List;

public interface IMarkerService {
    MarkerResponseTo create(MarkerRequestTo markerTo);
    MarkerResponseTo getById(BigInteger id);
    List<MarkerResponseTo> getAll();
    MarkerResponseTo update(MarkerRequestTo markerTo);
    boolean delete(BigInteger id);
}
