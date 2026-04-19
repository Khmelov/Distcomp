namespace Application.Exceptions.Application
{
    public class NewsAlreadyExistsException : AlreadyExistsException
    {
        public NewsAlreadyExistsException(string message) : base(message) { }
        
        public NewsAlreadyExistsException(string message, Exception inner) : base(message, inner) { }
    }
}