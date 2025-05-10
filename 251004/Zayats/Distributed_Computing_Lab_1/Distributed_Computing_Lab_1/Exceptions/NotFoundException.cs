namespace Distributed_Computing_Lab_1.Exceptions;

public class NotFoundException : Exception
{
    public int ErrorCode { get; }
    public NotFoundException(int errorCode, string message) : base(message)
    {
        ErrorCode = errorCode;
    }
}