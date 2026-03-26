namespace rest1.application.exceptions;
public class CreatorAlreadyExistsException : AlreadyExistsException
{
    public CreatorAlreadyExistsException(string message) : base(message) { }

    public CreatorAlreadyExistsException(string message, Exception inner) : base(message, inner) { }
}
