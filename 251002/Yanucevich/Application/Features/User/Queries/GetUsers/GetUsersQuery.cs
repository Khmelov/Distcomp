using Application.Abstractions;
using Domain.Projections;

namespace Application.Features.User.Queries;

public record class GetUsersQuery() : IQuery<IEnumerable<UserProjection>>;