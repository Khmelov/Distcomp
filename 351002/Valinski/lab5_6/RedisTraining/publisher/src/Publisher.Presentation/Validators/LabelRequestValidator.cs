using FluentValidation;
using Publisher.Presentation.Contracts;

namespace Publisher.Presentation.Validators;

public class LabelRequestValidator : AbstractValidator<LabelCreateRequest>
{
    public LabelRequestValidator()
    {
        RuleFor(x => x.Name)
            .NotEmpty().WithMessage("Название тега не может быть пустым.")
            .MinimumLength(2).WithMessage("Название тега должно содержать минимум 2 символа.")
            .MaximumLength(32).WithMessage("Название тега не может быть длиннее 32 символов.");
    }
}

public class LabelRequestUpdateValidator : AbstractValidator<LabelUpdateRequest>
{
    public LabelRequestUpdateValidator()
    {
        RuleFor(x => x.Name)
            .NotEmpty().WithMessage("Название тега не может быть пустым.")
            .MinimumLength(2).WithMessage("Название тега должно содержать минимум 2 символа.")
            .MaximumLength(32).WithMessage("Название тега не может быть длиннее 32 символов.");
    }
}
