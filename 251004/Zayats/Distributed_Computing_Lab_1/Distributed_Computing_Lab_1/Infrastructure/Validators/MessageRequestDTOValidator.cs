using Distributed_Computing_Lab_1.DTO.RequestDTO;
using FluentValidation;

namespace Distributed_Computing_Lab_1.Infrastructure.Validators;

public class MessageRequestDTOValidator : AbstractValidator<MessageRequestDTO>
{
    public MessageRequestDTOValidator()
    {
        RuleFor(dto => dto.Content).Length(2, 2048);
    }
}