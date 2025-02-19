using System;
using FluentValidation;

namespace Application.Features.User.Commands;

public class UpdateUserCommandValidator : AbstractValidator<UpdateUserCommand>
{
    public UpdateUserCommandValidator(){
        RuleFor(x => x.login).NotEmpty().Length(2, 64);
        RuleFor(x => x.password).NotEmpty().Length(8, 128);
        RuleFor(x => x.firstname).NotEmpty().Length(2, 64);
        RuleFor(x => x.lastname).NotEmpty().Length(2, 64);
    }

}
