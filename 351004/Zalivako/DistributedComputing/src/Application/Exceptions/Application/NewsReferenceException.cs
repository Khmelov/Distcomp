using Application.Exceptions.Application;

namespace Application.Exceptions.Application
{
    public class NewsReferenceException : ReferenceException
    {
        public NewsReferenceException(string message) : base(message) { }

        public NewsReferenceException(string message, Exception inner) : base(message, inner) { }
    }
}