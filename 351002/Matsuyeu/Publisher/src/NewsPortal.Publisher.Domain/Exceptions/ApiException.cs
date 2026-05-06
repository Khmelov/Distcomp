namespace Publisher.src.NewsPortal.Publisher.Domain.Exceptions;

public abstract class ApiException : Exception
{
    public int StatusCode { get; }
    public string ErrorCode { get; }

    protected ApiException(string message, int statusCode, string errorCode) : base(message)
    {
        StatusCode = statusCode;
        ErrorCode = errorCode;
    }
}