using Application.DTO.Request;
using FluentValidation;

namespace Application.Validators;

public class NewsRequestToValidator : AbstractValidator<NewsRequestTo>
{
    public NewsRequestToValidator()
    {
        RuleFor(news => news.UserId).NotEmpty();

        RuleFor(news => news.Title).Length(2, 64).NotEmpty();

        RuleFor(news => news.Content).Length(4, 2048).NotEmpty();
    }
}