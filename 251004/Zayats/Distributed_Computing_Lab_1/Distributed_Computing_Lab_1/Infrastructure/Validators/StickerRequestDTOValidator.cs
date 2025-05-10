using Distributed_Computing_Lab_1.DTO.RequestDTO;
using FluentValidation;

namespace Distributed_Computing_Lab_1.Infrastructure.Validators;

public class StickerRequestDTOValidator : AbstractValidator<StickerRequestDTO>
{
    public StickerRequestDTOValidator()
    {
        RuleFor(dto => dto.Name).Length(2, 32);
    }
}