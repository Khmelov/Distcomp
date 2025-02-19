using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.User.Queries;

public class GetUsersQueryHandler : IQueryHandler<GetUsersQuery, IEnumerable<UserProjection>>
{
    private readonly IUserRepository _userRepository;

    public GetUsersQueryHandler(IUserRepository userRepository)
    {
        _userRepository = userRepository;
    }

    public async Task<Result<IEnumerable<UserProjection>>> Handle(GetUsersQuery request, CancellationToken cancellationToken)
    {
        var result = await _userRepository.GetUsers();

        if(!result.IsSuccess){
            return Result.Failure<IEnumerable<UserProjection>>(result.Error);
        }

        return Result.Success(result.Value.Select(u => u.ToUserProjection()));
    }
}
