namespace Distcomp_NoteMicroservice.Validation;

public class ValidationError
{
    public string Property { get; init; } = string.Empty;
    
    public string Message { get; init; } = string.Empty;
    
    public string Code { get; init; } = string.Empty;

    public ValidationError(string property, string message, string code)
    {
        Property = property;
        Message = message;
        Code = code;
    }

    public override string ToString() => $"[{Property}] {Code}: {Message}";
}