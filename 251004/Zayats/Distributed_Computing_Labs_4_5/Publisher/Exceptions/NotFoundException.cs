namespace Publisher.Exceptions;

public class NotFoundException : Exception
{
    public int ErrorCode { get; }
    public NotFoundException(int errorCode, string message) : base(message)
    {
        ErrorCode = errorCode;
    }
}
