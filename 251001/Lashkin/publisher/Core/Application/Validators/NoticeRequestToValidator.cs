using Application.DTO.Request;
using FluentValidation;

namespace Application.Validators;

public class NoticeRequestToValidator : AbstractValidator<NoticeRequestTo>
{
    public NoticeRequestToValidator()
    {
        RuleFor(notice => notice.NewsId).NotEmpty();

        RuleFor(notice => notice.Content).Length(2, 2048).NotEmpty();
    }
}