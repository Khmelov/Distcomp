package by.bsuir.dc.lab1.validators;

import by.bsuir.dc.lab1.dto.CommentRequestTo;
import by.bsuir.dc.lab1.dto.EditorRequestTo;

public class CommentValidator {
    public static boolean validate(CommentRequestTo comment){
        boolean valid;
        valid = comment.getContent().length() > 1 && comment.getContent().length() < 2049;
        return valid;
    }
}
