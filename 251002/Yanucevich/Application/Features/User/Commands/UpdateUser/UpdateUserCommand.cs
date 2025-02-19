using Application.Abstractions;
using Domain.Projections;

namespace Application.Features.User.Commands;

public record class UpdateUserCommand
(
    long id,
    string login,
    string password,
    string lastname,
    string firstname
) : ICommand<UserProjection>;
