using FluentValidation;
using lab_1.Dtos.RequestDtos;
using lab_1.Entities;

namespace lab_1.Services.Validtors;



public class MarkerValidator:AbstractValidator<MarkerRequestDto>
{
    public MarkerValidator()
    {
        RuleFor(x => x.Name).Length(2, 32);
    }
}