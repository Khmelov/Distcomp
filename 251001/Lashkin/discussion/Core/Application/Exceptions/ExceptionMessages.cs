namespace Application.Exceptions;

public static class ExceptionMessages
{
    public const string UserNotFound = "User with {0} does not exist in the database.";
    public const string NewsNotFound = "News with {0} does not exist in the database.";
    public const string NoticeNotFound = "Notice with {0} does not exist in the database.";
    public const string LabelNotFound = "Label with {0} does not exist in the database.";

    public const string UserAlreadyExists = "User with {0} already exists in the database.";
    public const string NewsAlreadyExists = "News with {0} already exists in the database.";
    public const string LabelAlreadyExists = "Label with {0} already exists in the database.";
}