using FluentValidation;
using Presentation.Contracts.Requests;

namespace Presentation.Validators.TopicValidators;

public class TopicUpdateRequestValidator : AbstractValidator<TopicUpdateRequestTo>
{
    public TopicUpdateRequestValidator()
    {
        RuleFor(x => x.Title)
            .MinimumLength(2)
            .MaximumLength(64)
            .WithMessage("Invalid title length");

        RuleFor(x => x.Content)
            .MinimumLength(4)
            .MaximumLength(2048)
            .WithMessage("Invalid content length");
    }
}
