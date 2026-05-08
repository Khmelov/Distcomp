using FluentValidation;
using Presentation.Contracts.Requests;

namespace Presentation.Validators.UserValidators;

public class UserRequestValidator : AbstractValidator<UserRequestTo>
{
    public UserRequestValidator()
    {
        RuleFor(x => x.Login)
            .MinimumLength(2)
            .MaximumLength(64)
            .WithMessage("Invalid login length");

        RuleFor(x => x.Password)
            .MinimumLength(8)
            .MaximumLength(128)
            .WithMessage("Invalid password length");
        
        RuleFor(x => x.Firstname)
            .MinimumLength(2)
            .MaximumLength(64)
            .WithMessage("Invalid first name length");
        
        RuleFor(x => x.Lastname)
            .MinimumLength(2)
            .MaximumLength(64)
            .WithMessage("Invalid last name length");
    }
}
