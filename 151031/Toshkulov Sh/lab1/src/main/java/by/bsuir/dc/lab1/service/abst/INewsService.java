package by.bsuir.dc.lab1.service.abst;

import by.bsuir.dc.lab1.dto.*;

import java.math.BigInteger;
import java.util.List;

public interface INewsService {
    NewsResponseTo create(NewsRequestTo newsTo);

    NewsResponseTo getById(BigInteger id);

    List<NewsResponseTo> getAll();

    NewsResponseTo update(NewsRequestTo newsTo);

    boolean delete(BigInteger id);
}
