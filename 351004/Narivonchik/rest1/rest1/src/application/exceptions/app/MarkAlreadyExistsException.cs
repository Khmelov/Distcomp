namespace rest1.application.exceptions;

 public class MarkAlreadyExistsException : AlreadyExistsException
{
    public MarkAlreadyExistsException(string message) : base(message) { }

    public MarkAlreadyExistsException(string message, Exception inner) : base(message, inner) { }
}
