namespace rest1.application.exceptions;

public class UnauthorizedException : Exception
{
    public UnauthorizedException() : base() { }
    
    public UnauthorizedException(string message) : base(message) { }
    
    public UnauthorizedException(string message, Exception innerException) 
        : base(message, innerException) { }
}