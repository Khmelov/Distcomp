namespace Application.Exceptions
{
    public class PostNotFoundException : Exception
    {
        public PostNotFoundException(string message, Exception inner) : base(message, inner) { }

        public PostNotFoundException(string message) : base(message) { }
    }
}
