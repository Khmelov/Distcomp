package by.bsuir.discussion.exceptions;

public class EntityExistsException extends Exception {
    public EntityExistsException(String comment) {
        super(comment);
    }
}
