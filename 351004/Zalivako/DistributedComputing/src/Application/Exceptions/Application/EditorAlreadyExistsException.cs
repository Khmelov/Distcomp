namespace Application.Exceptions.Application
{
    public class EditorAlreadyExistsException : AlreadyExistsException
    {
        public EditorAlreadyExistsException(string message) : base(message) { }

        public EditorAlreadyExistsException(string message, Exception inner) : base(message, inner) { }
    }
}