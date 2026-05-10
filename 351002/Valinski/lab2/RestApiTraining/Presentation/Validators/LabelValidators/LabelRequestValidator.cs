using FluentValidation;
using Presentation.Contracts.Requests;

namespace Presentation.Validators.LabelValidators;

public class LabelRequestValidator : AbstractValidator<LabelRequestTo>
{
    public LabelRequestValidator()
    {
        ClassLevelCascadeMode = CascadeMode.Stop;

        RuleFor(x => x.Name)
            .MinimumLength(2)
            .MaximumLength(32)
            .WithMessage("Invalid name length");
    }
}
