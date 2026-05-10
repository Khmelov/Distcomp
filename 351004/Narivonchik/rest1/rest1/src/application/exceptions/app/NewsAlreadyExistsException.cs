namespace rest1.application.exceptions;

public class NewsAlreadyExistsException : AlreadyExistsException
{
    public NewsAlreadyExistsException(string message) : base(message) { }
        
    public NewsAlreadyExistsException(string message, Exception inner) : base(message, inner) { }
}