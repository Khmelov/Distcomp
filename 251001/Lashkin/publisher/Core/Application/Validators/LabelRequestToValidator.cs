using Application.DTO.Request;
using FluentValidation;

namespace Application.Validators;

public class LabelRequestToValidator : AbstractValidator<LabelRequestTo>
{
    public LabelRequestToValidator()
    {
        RuleFor(label => label.Name).Length(2, 32).NotEmpty();
    }
}