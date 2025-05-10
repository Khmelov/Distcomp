using Distributed_Computing_Lab_1.DTO.RequestDTO;
using FluentValidation;

namespace Distributed_Computing_Lab_1.Infrastructure.Validators;

public class TopicRequestDTOValidator : AbstractValidator<TopicRequestDTO>
{
    public TopicRequestDTOValidator()
    {
        RuleFor(dto => dto.Title).Length(2, 64);
        RuleFor(dto => dto.Content).Length(4, 2048);
    }
}