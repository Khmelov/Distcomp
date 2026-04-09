using FluentValidation;
using Presentation.Contracts.Requests;

namespace Presentation.Validators.ReactionValidators;

public class ReactionUpdateRequestValidator : AbstractValidator<ReactionUpdateRequestTo>
{
    public ReactionUpdateRequestValidator()
    {
        RuleFor(x => x.Content)
            .MinimumLength(2)
            .MaximumLength(2048)
            .WithMessage("Invalid content length");
    }
}
