namespace Shared.Commons;

public class Result<T> 
{
    public T? Value { get; private set; }
    public bool IsSuccess { get; private set; }
    public string? ErrorMessage { get; private set; }
    public ErrorType ErrorType { get; private set; }
    
    public static Result<T> Success(T value) => new()
    {
        Value = value,
        IsSuccess = true
    };
    
    public static Result<T> Failure(string errorMessage, ErrorType errorType = ErrorType.Failure) => new()
    {
        IsSuccess = false,
        ErrorMessage = errorMessage,
        ErrorType = errorType
    };
}

public class Result
{
    public bool IsSuccess { get; private set; }
    public string? ErrorMessage { get; private set; }
    public ErrorType ErrorType { get; private set; }
    
    public static Result Success() => new()
    {
        IsSuccess = true
    };
    
    public static Result Failure(string errorMessage, ErrorType errorType = ErrorType.Failure) => new()
    {
        IsSuccess = false,
        ErrorMessage = errorMessage,
        ErrorType = errorType
    };
}
