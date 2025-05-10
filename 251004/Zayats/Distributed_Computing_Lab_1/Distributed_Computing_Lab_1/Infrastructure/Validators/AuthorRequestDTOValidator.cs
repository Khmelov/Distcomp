using Distributed_Computing_Lab_1.DTO.RequestDTO;
using FluentValidation;

namespace Distributed_Computing_Lab_1.Infrastructure.Validators;

public class AuthorRequestDTOValidator : AbstractValidator<AuthorRequestDTO>
{
    public AuthorRequestDTOValidator()
    {
        RuleFor(dto => dto.Login).Length(2, 64);
        RuleFor(dto => dto.Password).Length(8, 128);
        RuleFor(dto => dto.Firstname).Length(2, 64);
        RuleFor(dto => dto.Lastname).Length(2, 64);
    }
}