using Application.DTO.Request;
using FluentValidation;

namespace Application.Validators;

public class UpdateNewsRequestToValidator : AbstractValidator<UpdateNewsRequestTo>
{
    public UpdateNewsRequestToValidator()
    {
        RuleFor(news => news.UserId).NotEmpty();

        RuleFor(news => news.Title).Length(2, 64).NotEmpty();

        RuleFor(news => news.Content).Length(4, 2048).NotEmpty();
    }
}