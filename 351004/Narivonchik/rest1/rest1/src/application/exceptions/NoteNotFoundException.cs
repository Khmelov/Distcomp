namespace rest1.application.exceptions;

public class NoteNotFoundException : NotFoundException
{
    public NoteNotFoundException(string message, Exception inner) : base(message, inner) { }

    public NoteNotFoundException(string message) : base(message) { }
}