using Distributed_Computing_Lab_2.DTO.RequestDTO;
using FluentValidation;

namespace Distributed_Computing_Lab_2.Infrastructure.Validators;

public class StickerRequestDTOValidator : AbstractValidator<StickerRequestDTO>
{
    public StickerRequestDTOValidator()
    {
        RuleFor(dto => dto.Name).Length(2, 32);
    }
}