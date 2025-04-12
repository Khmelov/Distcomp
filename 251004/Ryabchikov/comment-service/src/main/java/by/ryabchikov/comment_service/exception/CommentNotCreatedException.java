package by.ryabchikov.comment_service.exception;

public class CommentNotCreatedException extends RuntimeException {
    private CommentNotCreatedException(String message) {
        super(message);
    }

    public static CommentNotCreatedException byInvalidTweetId(Long tweetId) {
        return new CommentNotCreatedException(String.format("Comment not created, because tweet with id: %d not found", tweetId));
    }
}
