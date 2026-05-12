namespace Publisher.src.NewsPortal.Publisher.Domain.Exceptions;

public class ConflictException : ApiException
{
    public ConflictException(string message)
        : base(message, 403, GenerateErrorCode(403, message))
    {
    }

    private static string GenerateErrorCode(int statusCode, string message)
    {
        var hash = Math.Abs(message.GetHashCode()) % 100;
        return $"{statusCode}{hash:D2}";
    }
}
