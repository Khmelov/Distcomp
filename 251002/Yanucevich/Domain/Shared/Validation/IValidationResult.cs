using System;

namespace Domain.Shared.Validation;
public interface IValidationResult
{
    public static readonly Error ValidationError = new(
        403, 0, 4,
        "A validation problem occurred");

    Error[] Errors { get; }
}
