using Discussion.Presentation.Contracts;
using FluentValidation;

namespace Discussion.Presentation.Validators;

public class ReactionRequestValidator : AbstractValidator<ReactionRequest>
{
    public ReactionRequestValidator()
    {
        RuleFor(x => x.TopicId)
            .GreaterThan(0).WithMessage("TopicId должен быть больше нуля.");

        RuleFor(x => x.Country)
            .NotEmpty().WithMessage("Страна не может быть пустой.")
            .MaximumLength(64).WithMessage("Название страны не может быть длиннее 64 символов.");

        RuleFor(x => x.Content)
            .NotEmpty().WithMessage("Контент реакции не может быть пустым.")
            .MinimumLength(2).WithMessage("Контент реакции должен содержать минимум 2 символа.")
            .MaximumLength(2048).WithMessage("Контент реакции не может быть длиннее 2048 символов.");
    }
}

public class ReactionUpdateValidator : AbstractValidator<ReactionUpdateRequest>
{
    public ReactionUpdateValidator()
    {
        RuleFor(x => x.Id)
            .GreaterThan(0).WithMessage("Id реакции должен быть больше нуля.");

        RuleFor(x => x.TopicId)
            .GreaterThan(0).WithMessage("TopicId должен быть больше нуля.");

        RuleFor(x => x.Country)
            .NotEmpty().WithMessage("Страна не может быть пустой.")
            .MaximumLength(64).WithMessage("Название страны не может быть длиннее 64 символов.");

        RuleFor(x => x.Content)
            .NotEmpty().WithMessage("Контент реакции не может быть пустым.")
            .MinimumLength(2).WithMessage("Контент реакции должен содержать минимум 2 символа.")
            .MaximumLength(2048).WithMessage("Контент реакции не может быть длиннее 2048 символов.");
    }
}
