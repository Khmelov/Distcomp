using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.User.Queries;

public class GetUserByIdQueryHandler : IQueryHandler<GetUserByIdQuery, UserProjection>
{
    private readonly IUserRepository _userRepository;

    public GetUserByIdQueryHandler(IUserRepository userRepository)
    {
        _userRepository = userRepository;
    }

    public async Task<Result<UserProjection>> Handle(GetUserByIdQuery request, CancellationToken cancellationToken)
    {
        var resultUser = await _userRepository.GetUserById(request.id);

        if(!resultUser.IsSuccess){
            return Result.Failure<UserProjection>(resultUser.Error);
        }

        return resultUser.Value.ToUserProjection();
    }
}
