package by.bsuir.dc.lab1.validators;

import by.bsuir.dc.lab1.dto.EditorRequestTo;
public class EditorValidator {
    public static boolean validate(EditorRequestTo editor){
        boolean valid = true;
        valid &= editor.getLogin().length() > 1 && editor.getLogin().length() < 65;
        valid &= editor.getPassword().length() > 7 && editor.getLogin().length() < 129;
        valid &= editor.getFirstname().length() > 1 && editor.getFirstname().length() < 65;
        valid &= editor.getLastname().length() > 1 && editor.getLastname().length() < 65;
        return valid;
    }
}
