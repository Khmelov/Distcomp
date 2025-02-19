namespace Domain.Shared;

public class Result
{
    public bool IsSuccess { get; }
    public Error Error { get; }

    protected internal Result(bool isSuccess, Error error)
    {
        if(isSuccess && error!=Error.None){
            throw new ArgumentException("Result cannot be successful and have a not-None error.");
        }
        if(!isSuccess && error == Error.None){
            throw new ArgumentException("Result cannot be unsuccessful with a None error.");
        }
        
        IsSuccess = isSuccess;
        Error = error; 
    }

    public static Result Success() => new Result(true, Error.None);
    public static Result<TValue> Success<TValue>(TValue value) => new Result<TValue>(value, true, Error.None);

    public static Result Failure(Error error) => new Result(false, error);
    public static Result<TValue> Failure<TValue>(Error error) => new Result<TValue>(default, false, error);
    
    public static Result<TValue> Create<TValue>(TValue? value) => value is not null 
        ? Success<TValue>(value) 
        : Failure<TValue>(Error.NullValue);
}