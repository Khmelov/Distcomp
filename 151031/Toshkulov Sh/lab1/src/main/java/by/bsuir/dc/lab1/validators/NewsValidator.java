package by.bsuir.dc.lab1.validators;

import by.bsuir.dc.lab1.dto.MarkerRequestTo;
import by.bsuir.dc.lab1.dto.NewsRequestTo;

public class NewsValidator {
    public static boolean validate(NewsRequestTo news){
        boolean valid = true;
        valid &= news.getTitle().length() > 1 && news.getTitle().length() < 65;
        valid &= news.getContent().length() > 3 && news.getContent().length() < 2049;
        return valid;
    }
}
