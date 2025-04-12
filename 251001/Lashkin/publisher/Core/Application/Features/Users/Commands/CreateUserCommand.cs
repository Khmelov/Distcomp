using Application.DTO.Request;
using Application.DTO.Response;
using MediatR;

namespace Application.Features.Users.Commands;

public record CreateUserCommand(UserRequestTo UserRequestTo) : IRequest<UserResponseTo>;