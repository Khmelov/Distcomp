using Application.DTO.Request;
using Application.DTO.Response;
using MediatR;

namespace Application.Features.Users.Commands;

public record UpdateUserCommand(long Id, UserRequestTo UserRequestTo) : IRequest<UserResponseTo>;