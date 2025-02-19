using Application.Abstractions;

namespace Application.Features.User.Commands;

public record class DeleteUserCommand
(
    long id
) : ICommand;
