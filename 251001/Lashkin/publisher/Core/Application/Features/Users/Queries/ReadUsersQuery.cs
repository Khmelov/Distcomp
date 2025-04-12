using Application.DTO.Response;
using MediatR;

namespace Application.Features.Users.Queries;

public record ReadUsersQuery() : IRequest<IEnumerable<UserResponseTo>>;