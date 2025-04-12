using MediatR;

namespace Application.Features.Users.Commands;

public record DeleteUserCommand(long Id) : IRequest;