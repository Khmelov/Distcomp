package by.ryabchikov.comment_service.exception;

public class CommentNotFoundException extends RuntimeException {
    private CommentNotFoundException(String message) {
        super(message);
    }

    public static CommentNotFoundException byId(Long id) {
        return new CommentNotFoundException(String.format("Comment with id %d not found", id));
    }
}
