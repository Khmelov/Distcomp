using FluentValidation;
using lab_1.Dtos.RequestDtos;
using lab_1.Entities;

namespace lab_1.Services.Validtors;

public class StoryValidator:AbstractValidator<StoryRequestDto>
{
    public StoryValidator()
    {
        RuleFor(x => x.Content).Length(4, 2048);
        RuleFor(x => x.Title).Length(2, 64).NotEmpty();
        RuleFor(x => x.AuthorId).NotEmpty();
    }
}