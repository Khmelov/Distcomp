using Application.Abstractions;
using Domain.Projections;

namespace Application.Features.User.Commands;

public record class CreateUserCommand
( 
    string login, 
    string password, 
    string firstname,
    string lastname
) : ICommand<UserProjection>;