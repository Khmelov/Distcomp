using Application.DTO.Response;
using MediatR;

namespace Application.Features.Users.Queries;

public record ReadUserQuery(long Id) : IRequest<UserResponseTo>;