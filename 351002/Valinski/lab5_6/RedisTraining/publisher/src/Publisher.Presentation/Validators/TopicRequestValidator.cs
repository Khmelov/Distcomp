using FluentValidation;
using Publisher.Presentation.Contracts;

namespace Publisher.Presentation.Validators;

public class TopicRequestValidator : AbstractValidator<TopicCreateRequest>
{
    public TopicRequestValidator()
    {
        RuleFor(x => x.UserId)
            .GreaterThan(0).WithMessage("Некорректный ID пользователя.");
            
        RuleFor(x => x.Title)
            .NotEmpty().WithMessage("Заголовок не может быть пустым.")
            .MinimumLength(2).WithMessage("Заголовок должен содержать минимум 2 символа.")
            .MaximumLength(64).WithMessage("Заголовок не может быть длиннее 64 символов.");
            
        RuleFor(x => x.Content)
            .NotEmpty().WithMessage("Содержание не может быть пустым.")
            .MinimumLength(4).WithMessage("Содержание должно содержать минимум 4 символа.")
            .MaximumLength(2048).WithMessage("Содержание не может быть длиннее 2048 символов.");
    }
}

public class TopicRequestUpdateValidator : AbstractValidator<TopicUpdateRequest>
{
    public TopicRequestUpdateValidator()
    {
        RuleFor(x => x.Title)
            .NotEmpty().WithMessage("Заголовок не может быть пустым.")
            .MinimumLength(2).WithMessage("Заголовок должен содержать минимум 2 символа.")
            .MaximumLength(64).WithMessage("Заголовок не может быть длиннее 64 символов.");
            
        RuleFor(x => x.Content)
            .NotEmpty().WithMessage("Содержание не может быть пустым.")
            .MinimumLength(4).WithMessage("Содержание должно содержать минимум 4 символа.")
            .MaximumLength(2048).WithMessage("Содержание не может быть длиннее 2048 символов.");
    }
}
