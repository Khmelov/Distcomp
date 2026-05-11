using FluentValidation;
using lab_1.Dtos.RequestDtos;
using lab_1.Entities;

namespace lab_1.Services.Validtors;

public class CommentValidator:AbstractValidator<CommentRequestDto>
{
    public CommentValidator()
    {
        RuleFor(x => x.Content).Length(2,2048);
    }
}