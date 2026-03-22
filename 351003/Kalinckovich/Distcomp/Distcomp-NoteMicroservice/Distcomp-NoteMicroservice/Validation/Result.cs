namespace Distcomp_NoteMicroservice.Validation;

public class Result<T>
{
    public bool IsSuccess { get; init; }
    public T? Value { get; init; }
    public IReadOnlyList<ValidationError> Errors { get; init; } = Array.Empty<ValidationError>();

    public static Result<T> Success(T value) => new() { IsSuccess = true, Value = value };
    public static Result<T> Failure(params ValidationError[] errors)
        => new() { IsSuccess = false, Errors = errors };

    public static Result<T> Failure(string property, string message, string code)
        => Failure(new ValidationError(property, message, code));
}