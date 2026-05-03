using FluentValidation;
using Publisher.Presentation.Contracts;

public class UserValidator : AbstractValidator<UserCreateRequest>
{
    public UserValidator()
    {
        RuleFor(x => x.Login)
            .NotEmpty().WithMessage("Логин не может быть пустым.")
            .Length(2, 64).WithMessage("Логин должен содержать от 2 до 64 символов.");

        RuleFor(x => x.Password)
            .NotEmpty().WithMessage("Пароль не может быть пустым.")
            .Length(8, 128).WithMessage("Пароль должен содержать от 8 до 128 символов.");

        RuleFor(x => x.Firstname)
            .NotEmpty().WithMessage("Имя не может быть пустым.")
            .Length(2, 64).WithMessage("Имя должно содержать от 2 до 64 символов.");

        RuleFor(x => x.Lastname)
            .NotEmpty().WithMessage("Фамилия не может быть пустой.")
            .Length(2, 64).WithMessage("Фамилия должна содержать от 2 до 64 символов.");
    }
}
