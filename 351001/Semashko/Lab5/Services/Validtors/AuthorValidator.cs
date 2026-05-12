using FluentValidation;
using lab_1.Dtos.RequestDtos;
using lab_1.Entities;

namespace lab_1.Services.Validtors;

public class AuthorValidator:AbstractValidator<AuthorRequestDto>
{
    public AuthorValidator()
    {
        RuleFor(x => x.Login).Length(2, 64).NotEmpty();
        RuleFor(x => x.Password).Length(8, 128).NotEmpty();
        RuleFor(x => x.Firstname).Length(2, 64).NotEmpty();
        RuleFor(x => x.Lastname).Length(2, 64).NotEmpty();
    }
}