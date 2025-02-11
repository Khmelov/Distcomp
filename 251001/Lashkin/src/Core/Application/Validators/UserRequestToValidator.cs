using Application.DTO.Request;
using FluentValidation;

namespace Application.Validators;

public class UserRequestToValidator : AbstractValidator<UserRequestTo>
{
    public UserRequestToValidator()
    {
        RuleFor(user => user.Login).Length(2, 64);

        RuleFor(user => user.Password).Length(8, 128);

        RuleFor(user => user.FirstName).Length(2, 64);

        RuleFor(user => user.LastName).Length(2, 64);
    }
}