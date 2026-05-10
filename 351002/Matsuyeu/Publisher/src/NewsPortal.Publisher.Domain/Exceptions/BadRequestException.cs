namespace Publisher.src.NewsPortal.Publisher.Domain.Exceptions;

public class BadRequestException : ApiException
{
    public BadRequestException(string message)
        : base(message, 400, GenerateErrorCode(400, message))
    {
    }

    private static string GenerateErrorCode(int statusCode, string message)
    {
        var hash = Math.Abs(message.GetHashCode()) % 100;
        return $"{statusCode}{hash:D2}";
    }
}