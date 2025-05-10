namespace Distributed_Computing_Lab_1.Exceptions;

public class ConflictException : Exception
{
    public int ErrorCode { get; }
    public ConflictException(int errorCode, string message) : base(message)
    {
        ErrorCode = errorCode;
    }
}