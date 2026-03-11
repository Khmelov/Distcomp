namespace Application.Exceptions
{
    public class EditorNotFoundException : NotFoundException
    {
        public EditorNotFoundException(string message, Exception inner) : base(message, inner) { }

        public EditorNotFoundException(string message) : base(message) { }
    }
}
