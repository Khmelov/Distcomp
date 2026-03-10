namespace Application.Exceptions.Application
{
    public class MarkerNotFoundException : NotFoundException
    {
        public MarkerNotFoundException(string message, Exception inner) : base(message, inner) { }

        public MarkerNotFoundException(string message) : base(message) { }
    }
}
