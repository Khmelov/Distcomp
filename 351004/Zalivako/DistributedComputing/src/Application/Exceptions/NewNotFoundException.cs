namespace Application.Exceptions
{
    public class NewNotFoundException : NotFoundException
    {
        public NewNotFoundException(string message, Exception inner) : base(message, inner) { }

        public NewNotFoundException(string message) : base(message) { }
    }
}
