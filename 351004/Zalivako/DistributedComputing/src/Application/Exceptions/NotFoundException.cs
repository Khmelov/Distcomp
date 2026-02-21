namespace Application.Exceptions
{
    public class NotFoundException : Exception
    {
        public NotFoundException(string message, Exception inner) : base(message, inner) { }

        public NotFoundException(string message) : base(message) { }
    }
}
