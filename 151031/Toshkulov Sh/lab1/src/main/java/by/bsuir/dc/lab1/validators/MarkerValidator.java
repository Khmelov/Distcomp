package by.bsuir.dc.lab1.validators;


import by.bsuir.dc.lab1.dto.MarkerRequestTo;

public class MarkerValidator {
    public static boolean validate(MarkerRequestTo marker){
        boolean valid;
        valid = marker.getName().length() > 1 && marker.getName().length() < 33;
        return valid;
    }
}
