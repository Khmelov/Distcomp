namespace Application.Exceptions.Application
{
    public class ReferenceException : Exception
    {
        public ReferenceException(string message) : base(message) { }

        public ReferenceException(string message, Exception inner) : base(message, inner) { }
    }
}
