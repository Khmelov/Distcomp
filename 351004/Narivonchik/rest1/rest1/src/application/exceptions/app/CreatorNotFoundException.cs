namespace rest1.application.exceptions;

public class CreatorNotFoundException : NotFoundException
{
    public CreatorNotFoundException(string message, Exception inner) : base(message, inner) { }

    public CreatorNotFoundException(string message) : base(message) { }
}