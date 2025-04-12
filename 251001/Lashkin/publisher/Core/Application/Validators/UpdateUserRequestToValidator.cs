using Application.DTO.Request;
using FluentValidation;

namespace Application.Validators;

public class UpdateUserRequestToValidator : AbstractValidator<UpdateUserRequestTo>
{
    public UpdateUserRequestToValidator()
    {
        RuleFor(user => user.Login).Length(2, 64).NotEmpty();

        RuleFor(user => user.Password).Length(8, 128).NotEmpty();

        RuleFor(user => user.FirstName).Length(2, 64).NotEmpty();

        RuleFor(user => user.LastName).Length(2, 64).NotEmpty();
    }
}