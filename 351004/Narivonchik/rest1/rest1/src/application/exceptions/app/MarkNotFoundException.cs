namespace rest1.application.exceptions;

public class MarkNotFoundException : NotFoundException
{
    public MarkNotFoundException(string message, Exception inner) : base(message, inner) { }

    public MarkNotFoundException(string message) : base(message) { }
}