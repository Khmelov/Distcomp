namespace rest1.application.exceptions;

public class AlreadyExistsException : Exception
{
    public AlreadyExistsException(string message) : base(message) { }

    public AlreadyExistsException(string message, Exception inner) : base(message, inner) { }

}