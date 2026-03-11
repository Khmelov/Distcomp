namespace rest1.application.exceptions;
public class NoteAlreadyExistsException : AlreadyExistsException
{
    public NoteAlreadyExistsException(string message) : base(message) { }

    public NoteAlreadyExistsException(string message, Exception inner) : base(message, inner) { }
}
