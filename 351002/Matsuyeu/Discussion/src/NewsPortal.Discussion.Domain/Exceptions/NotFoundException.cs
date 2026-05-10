namespace Discussion.src.NewsPortal.Discussion.Domain.Exceptions;

public class NotFoundException : ApiException
{
    public NotFoundException(string message)
        : base(message, 404, GenerateErrorCode(404, message))
    {
    }

    private static string GenerateErrorCode(int statusCode, string message)
    {
        var hash = Math.Abs(message.GetHashCode()) % 100;
        return $"{statusCode}{hash:D2}";
    }
}