namespace Application.Exceptions.Application
{
    public class PostAlreadyExistsException : AlreadyExistsException
    {
        public PostAlreadyExistsException(string message) : base(message) { }

        public PostAlreadyExistsException(string message, Exception inner) : base(message, inner) { }
    }
}