using FluentValidation;
using Presentation.Contracts.Requests;

namespace Presentation.Validators.LabelValidators;

public class LabelUpdateRequestValidator : AbstractValidator<LabelUpdateRequestTo>
{
    public LabelUpdateRequestValidator()
    {
        RuleFor(x => x.Name)
            .MinimumLength(2)
            .MaximumLength(32)
            .WithMessage("Invalid name length");
    }
}
