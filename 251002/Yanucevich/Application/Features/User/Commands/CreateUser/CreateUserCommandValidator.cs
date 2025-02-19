using System;
using System.Data;
using FluentValidation;

namespace Application.Features.User.Commands;

public class CreateUserCommandValidator : AbstractValidator<CreateUserCommand>
{
    public CreateUserCommandValidator(){
        RuleFor(x => x.login).NotEmpty().Length(2, 64);
        RuleFor(x => x.password).NotEmpty().Length(8, 128);
        RuleFor(x => x.firstname).NotEmpty().Length(2, 64);
        RuleFor(x => x.lastname).NotEmpty().Length(2, 64);
    }
}
