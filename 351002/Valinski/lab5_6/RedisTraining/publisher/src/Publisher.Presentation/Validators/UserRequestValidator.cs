using FluentValidation;
using Publisher.Presentation.Contracts;

namespace Publisher.Presentation.Validators;

public class UserRequestValidator : AbstractValidator<UserCreateRequest>
{
    public UserRequestValidator()
    {
        RuleFor(x => x.Login)
            .NotEmpty().WithMessage("Логин не может быть пустым.")
            .MinimumLength(3).WithMessage("Логин должен содержать минимум 3 символа.")
            .MaximumLength(64).WithMessage("Логин не может быть длиннее 64 символов.");

        RuleFor(x => x.Password)
            .NotEmpty().WithMessage("Пароль не может быть пустым.")
            .MinimumLength(8).WithMessage("Пароль должен содержать минимум 6 символов.")
            .MaximumLength(128).WithMessage("Пароль не может быть длиннее 128 символов.");

        RuleFor(x => x.Firstname)
            .MinimumLength(2)
            .MaximumLength(64).WithMessage("Имя не может быть длиннее 64 символов.");

        RuleFor(x => x.Lastname)
            .MinimumLength(2)
            .MaximumLength(64).WithMessage("Фамилия не может быть длиннее 64 символов.");
    }
}

public class UserRequestUpdateValidator : AbstractValidator<UserUpdateRequest>
{
    public UserRequestUpdateValidator()
    {
        RuleFor(x => x.Login)
            .NotEmpty().WithMessage("Логин не может быть пустым.")
            .MinimumLength(3).WithMessage("Логин должен содержать минимум 3 символа.")
            .MaximumLength(64).WithMessage("Логин не может быть длиннее 64 символов.");

        RuleFor(x => x.Password)
            .NotEmpty().WithMessage("Пароль не может быть пустым.")
            .MinimumLength(8).WithMessage("Пароль должен содержать минимум 6 символов.")
            .MaximumLength(128).WithMessage("Пароль не может быть длиннее 128 символов.");

        RuleFor(x => x.Firstname)
            .MinimumLength(2)
            .MaximumLength(64).WithMessage("Имя не может быть длиннее 64 символов.");

        RuleFor(x => x.Lastname)
            .MinimumLength(2)
            .MaximumLength(64).WithMessage("Фамилия не может быть длиннее 64 символов.");
    }
}
