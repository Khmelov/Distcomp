using Application.Abstractions;
using Domain.Projections;

namespace Application.Features.User.Queries;

public record class GetUserByIdQuery
(
    long id
) : IQuery<UserProjection>;