using Application.DTO.Request;
using FluentValidation;

namespace Application.Validators;

public class UpdateLabelRequestToValidator : AbstractValidator<UpdateLabelRequestTo>
{
    public UpdateLabelRequestToValidator()
    {
        RuleFor(label => label.Name).Length(2, 32).NotEmpty();
    }
}