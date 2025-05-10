using Discussion.DTO.Request;
using FluentValidation;

namespace Discussion.Infrastructure.Validators;

public class MessageRequestDtoValidator : AbstractValidator<MessageRequestDTO>
{
    public MessageRequestDtoValidator()
    {
        RuleFor(dto => dto.Content).Length(2, 2048);
    }
}