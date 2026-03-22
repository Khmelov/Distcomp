namespace Distcomp_NoteMicroservice.Validation;

public sealed class ValidationResult
{
    public bool IsValid => !Errors.Any();
    public IReadOnlyList<ValidationError> Errors { get; init; } = Array.Empty<ValidationError>();
    
    public static ValidationResult Success => new();
    
    public static ValidationResult Failure(params ValidationError[] errors)
        => new() { Errors = errors };

    public static ValidationResult Failure(string property, string message, string code)
        => Failure(new ValidationError(property, message, code));
}