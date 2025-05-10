using FluentValidation;
using Publisher.DTO.RequestDTO;

namespace Publisher.Infrastructure.Validators;

public class StickerRequestDTOValidator : AbstractValidator<StickerRequestDTO>
{
    public StickerRequestDTOValidator()
    {
        RuleFor(dto => dto.Name).Length(2, 32);
    }
}